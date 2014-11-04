package actors

import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorRef, Props, Actor}
import play.api.libs.json.JsValue

class ClientTalkActor(out: ActorRef, manager: ActorRef) extends Actor with DeserializeMessages {

  def receive = deserialize {
    case json: JsValue => out ! json
  }

  override def preStart(): Unit = {
    manager ! RegisterClient()
  }

  override def postStop(): Unit = {
    manager ! UnregisterClient()
  }
}

object ClientTalkActor {
  def props(out: ActorRef, manager: ActorRef) = Props(classOf[ClientTalkActor], out, manager)
}