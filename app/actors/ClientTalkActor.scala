package actors

import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import play.api.libs.json.{Json, JsValue}
import protocol.{EnvelopeTimeStamp, Envelope, TextMessage}

class ClientTalkActor(name: String, out: ActorRef, manager: ActorRef) extends Actor with ActorLogging with DeserializeMessages {

  def receive = deserialize {
    case message: Envelope with EnvelopeTimeStamp => {
      out ! Json.toJson(message)
    }
    case message: Envelope => {
      log.debug(s"$name sending to manager")
      manager ! message
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