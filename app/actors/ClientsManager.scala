package actors

import actors.DatabaseProtocol.{StoredMessages, RecoverMessage, StoreMessage}
import actors.ManagerProtocol.{GiveAllOnline, CheckFriendsAvailability, UnregisterClient, RegisterClient}
import akka.actor.{Props, ActorLogging, ActorRef, Actor}
import akka.util.Timeout
import org.joda.time.DateTime
import play.api.libs.json.Json
import protocol._
import protocol.MessageTypes._
import services.DatabaseService
import akka.pattern.{ask, pipe}
import scala.concurrent.duration._

class ClientsManager(database: ActorRef) extends Actor with ActorLogging {
  assert(database != null)

  implicit val timeout: Timeout = 5 seconds

  var clients = Map.empty[String, Set[ActorRef]]

  def receive = {
    case RegisterClient(name, channel) => {
      clients = clients.get(name).map { channels =>
        log.debug(s"Register $name")
        clients.updated(name, channels + channel)
      } getOrElse {
        log.debug(s"Register $name")
        database ! RecoverMessage(name)
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
      val newMsg = new Envelope(msg.to, msg.uuid, msg.kind, msg.payload) with EDated with ESender {
        val date = new DateTime()
        val from = msg.from
      }
      log.debug(s"Received message from ${newMsg.from} to ${newMsg.to}")
      msg.kind match {
        case TextMessageType => newMsg.to.map { receiver =>
          clients.get(receiver).map { sockets =>
            sockets.foreach { actor =>
              log.debug("Receiver is connected, redirecting")
              actor ! newMsg
            }
          } getOrElse {
            log.debug("Receiver is not connected")
            val message = Json.fromJson[TextMessage](msg.payload).get
            database ! StoreMessage(newMsg)
          }
        }
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
    case unknown => log.error(s"Unknown message ${unknown.toString}")
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
  def props(database: DatabaseService): Props = Props(classOf[ClientsManager], database.database)
  case class Client(name: String, channel: ActorRef)
}

sealed trait ManagerProtocol
object ManagerProtocol {
  case class RegisterClient(name: String, channel: ActorRef) extends ManagerProtocol
  case class UnregisterClient(name: String, channel: ActorRef) extends ManagerProtocol
  case class CheckFriendsAvailability(friends: Iterable[String]) extends ManagerProtocol
  case object GiveAllOnline extends ManagerProtocol

  case class FriendsList(friends: Map[String, Boolean]) extends ManagerProtocol
}

