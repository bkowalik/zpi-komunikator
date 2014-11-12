package actors

import java.util.UUID

import actors.DatabaseProtocol.{StoredMessages, RecoverMessage, StoreMessage}
import actors.FileProtocol.Diff
import actors.ManagerProtocol.{GiveAllOnline, CheckFriendsAvailability, UnregisterClient, RegisterClient}
import akka.actor.{Props, ActorLogging, ActorRef, Actor}
import akka.util.Timeout
import org.joda.time.DateTime
import play.api.libs.json.Json
import protocol._
import protocol.MessageTypes._
import akka.pattern.{ask, pipe}
import scala.concurrent.duration._

class ClientsManager() extends Actor with ActorLogging {
  import context.dispatcher

  implicit val timeout: Timeout = 5 seconds

  var clients = Map.empty[String, Set[ActorRef]]

  var diffSyncs = Map.empty[UUID, ActorRef]

  context.system.scheduler.schedule(Duration.Zero, 20 seconds, self, KeepAlive)

  def receive = {
    case RegisterClient(name, channel) => {
      clients = clients.get(name).map { channels =>
        log.debug(s"Register $name")
        clients.updated(name, channels + channel)
      } getOrElse {
        log.debug(s"Register $name")
        //database ! RecoverMessage(name)
        self ! UserLoggedIn(name)
        clients.updated(name, Set(channel))
      }
    }

    case UnregisterClient(name, channel) => {
      clients = clients.get(name).map { channels =>
        log.debug(s"Unregister $name")
        if((channels - channel).isEmpty) {
          self ! UserLoggedOut(name)
          clients - name
        } else {
          clients.updated(name, channels - channel)
        }
      } getOrElse {
        log.warning(s"Unregister not registered client $name")
        clients
      }
    }

    case msg: Envelope with ESender => {
      log.debug("Get message")
      log.debug(s"Received message from ${msg.from} to ${msg.to}")
      msg.kind match {
        case TextMessageType => {
          val newMsg = new Envelope(msg.to, msg.uuid, msg.kind, msg.payload) with EDated with ESender {
            val date = new DateTime()
            val from = msg.from
          }
          msg.to.filter(_ != newMsg.from).map { receiver =>
            clients.get(receiver).map { sockets =>
              sockets.foreach { actor =>
                log.debug("Receiver is connected, redirecting")
                actor ! newMsg
              }
            } getOrElse {
              log.debug("Receiver is not connected")
              val message = Json.fromJson[TextMessage](msg.payload).get
              //database ! StoreMessage(newMsg)
            }
          }
        }
        /*case DiffSyncType => {
          Json.fromJson[DiffSync](msg.payload) match {
            case NewSession(text) => {
              val uuid = UUID.randomUUID()
              val newMsg = new Envelope(msg.to, Option(uuid.toString), msg.kind, msg.payload) with EDated with ESender {
                val date = new DateTime()
                val from = msg.from
              }
              val shadows = msg.to.flatMap { client =>
                clients.get(client)
              }.flatten.map(_ -> text).toMap

              val fileActor = context.actorOf(FileActor.props(text, shadows))
              diffSyncs = diffSyncs + (uuid -> fileActor)

              shadows.keys.foreach(_ ! newMsg)
            }
            case Patch(text) => {
              val uuid = UUID.fromString(msg.uuid.get)
              diffSyncs.get(uuid).foreach(_ ! Diff(text))
            }
          }
        }*/
      }
    }

    case CheckFriendsAvailability(friends) => {
      clients.filterKeys(friends.toSet).map {
        case (client, connections) => client -> connections.nonEmpty
      }
    }

    case StoredMessages(to, messages) => {
      clients.get(to).map { channels =>
        log.debug("Sending stored messages")
      }
    }

    case com: UserLoggedIn => notifyUserLoggedIn(com)
    case com: UserLoggedOut => notifyUserLoggedOut(com)
    case GiveAllOnline => sender() ! clients.keys
    case KeepAlive => clients.map {
      case (client, connections) => {
        val msg = Envelope.keepAliveMessage(client)
        connections.foreach(_ ! msg)
      }
    }
    case unknown => log.warning(s"Unknown message ${unknown.toString}")
  }

  def notifyUserLoggedIn(com: UserLoggedIn) = {
    clients.filterKeys(_ != com.username).map {
      case (name, actors) => {
        val envelope = new Envelope(Set(name), None, MessageTypes.UserLoggedInType, Json.toJson(com)) with EDated {
          val date: DateTime = new DateTime()
        }
        actors foreach (_ ! envelope)
      }
    }
  }

  def notifyUserLoggedOut(com: UserLoggedOut) = {
    clients.filterKeys(_ != com.username).map {
      case (name, actors) => {
        val envelope = new Envelope(Set(name), None, MessageTypes.UserLoggedOutType, Json.toJson(com)) with EDated {
          val date: DateTime = new DateTime()
        }
        actors foreach (_ ! envelope)
      }
    }
  }
}

object ClientsManager {
  def props(): Props = Props(classOf[ClientsManager])
  case class Client(name: String, channel: ActorRef)
}

sealed trait ManagerProtocol
object ManagerProtocol {
  case class RegisterClient(name: String, channel: ActorRef) extends ManagerProtocol
  case class UnregisterClient(name: String, channel: ActorRef) extends ManagerProtocol
  case class CheckFriendsAvailability(friends: Iterable[String]) extends ManagerProtocol
  case object GiveAllOnline extends ManagerProtocol
  case object KeepAlive extends ManagerProtocol

  case class FriendsList(friends: Map[String, Boolean]) extends ManagerProtocol
}

