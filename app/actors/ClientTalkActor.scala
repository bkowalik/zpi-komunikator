package actors

import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorRef, Props, Actor}
import play.api.libs.json.JsValue
import protocol.{Envelope, TextMessage}

class ClientTalkActor(name: String, out: ActorRef, manager: ActorRef) extends Actor with DeserializeMessages {

  def receive = deserialize {
    case message: Envelope => manager ! message
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