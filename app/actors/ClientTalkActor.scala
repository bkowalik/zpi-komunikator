package actors

import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorRef, Props, Actor}
import play.api.libs.json.JsValue

class ClientTalkActor(name: String, out: ActorRef, manager: ActorRef) extends Actor with DeserializeMessages {

  def receive = deserialize {
    case json: JsValue => out ! json
  }

  override def preStart(): Unit = {
    //manager ! RegisterClient()
  }

  override def postStop(): Unit = {
    //manager ! UnregisterClient()
  }
}

object ClientTalkActor {
  def props(name: String, out: ActorRef, manager: ActorRef) = Props(classOf[ClientTalkActor], name, out, manager)
}