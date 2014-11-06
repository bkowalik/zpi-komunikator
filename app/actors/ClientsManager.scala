package actors

import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorLogging, ActorRef, Actor}
import protocol.Envelope
import protocol.MessageTypes._

class ClientsManager extends Actor with ActorLogging {

  var clients = Map.empty[String, Set[ActorRef]]

  def receive = {
    case RegisterClient(name, channel) => {
      clients = clients.get(name).map { channels =>
        clients.updated(name, channels + channel)
      } getOrElse {
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
      msg.kind match {
        case TextMessageType => clients.get(msg.to.get).map { sockets =>
          sockets.foreach { actor =>
            actor ! msg
          }
        } getOrElse {
          log.debug(s"No such receiver: ${msg.to.get}")
        }
      }
    }
    case _ => ???
  }
}

object ClientsManager {
  case class Client(name: String, channel: ActorRef)
}

sealed trait ManagerProtocol
object ManagerProtocol {
  case class RegisterClient(name: String, channel: ActorRef) extends ManagerProtocol
  case class UnregisterClient(name: String, channel: ActorRef) extends ManagerProtocol
}

