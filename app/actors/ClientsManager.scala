package actors

import actors.DatabaseProtocol.{StoredMessages, RecoverMessage, StoreMessage}
import actors.ManagerProtocol.{CheckFriendsAvailability, UnregisterClient, RegisterClient}
import akka.actor.{Props, ActorLogging, ActorRef, Actor}
import akka.util.Timeout
import org.joda.time.DateTime
import play.api.libs.json.Json
import protocol.{EnvelopeTimeStamp, TextMessage, Envelope}
import protocol.MessageTypes._
import services.DatabaseService
import akka.pattern.{ask, pipe}
import scala.concurrent.duration._

class ClientsManager(database: ActorRef) extends Actor with ActorLogging {

  implicit val timeout: Timeout = 5 seconds

  var clients = Map.empty[String, Set[ActorRef]]

  def receive = {
    case RegisterClient(name, channel) => {
      clients = clients.get(name).map { channels =>
        clients.updated(name, channels + channel)
      } getOrElse {
        database ! RecoverMessage(name)
        clients.updated(name, Set(channel))
      }
    }
    case UnregisterClient(name, channel) => {
      clients = clients.get(name).map { channels =>
        clients.updated(name, channels - channel)
      } getOrElse {
        log.warning(s"Unregister not registered client $name")
        clients
      }
    }
    case msg: Envelope => {
      log.debug(s"Received message from ${msg.from} to ${msg.to}")
      msg.kind match {
        case TextMessageType => clients.get(msg.to.get).map { sockets =>
          sockets.foreach { actor =>
            log.debug("Receiver is connected, redirecting")
            actor ! msg
          }
        } getOrElse {
          val message = Json.fromJson[TextMessage](msg.payload).get
          database ! StoreMessage(msg.from.get, message.message)
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
        log.debug("Sending sotred messages")
      }
    }
    case unknown => log.error(s"Unknown message ${unknown.toString}")
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

  case class FriendsList(friends: Map[String, Boolean]) extends ManagerProtocol
}

