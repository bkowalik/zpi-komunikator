package actors

import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import play.api.libs.json.JsValue
import protocol.{Envelope, TextMessage}

class ClientTalkActor(name: String, out: ActorRef, manager: ActorRef) extends Actor with ActorLogging with DeserializeMessages {

  def receive = deserialize {
    case message: Envelope => {
      if(sender() == manager) {
        log.debug(s"Message from: ${message.from}")
        out ! message
      } else {
        log.debug(s"Message to: ${message.to}")
        manager ! message
      }
    }
  }

  override def preStart(): Unit = {
    manager ! RegisterClient(name, self)
  }

  override def postStop(): Unit = {
    manager ! UnregisterClient(name, self)
  }
}

object ClientTalkActor {
  def props(name: String, out: ActorRef, manager: ActorRef) = Props(classOf[ClientTalkActor], name, out, manager)
}