package actors

import actors.FileProtocol.Diff
import actors.ManagerProtocol.{UnregisterClient, RegisterClient}
import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import play.api.libs.json.{Json, JsValue}
import protocol._
import services.ManagerService

class ClientTalkActor(val name: String, out: ActorRef, manager: ActorRef) extends Actor with ActorLogging with DeserializeMessages {
  assert(out != null)
  assert(manager != null)

  def receive = deserialize {
    case message: Envelope with EDated => {
      log.debug(s"$name sending to output")
      out ! Json.toJson(message)
    }

    case message: Envelope with ESender => {
      log.debug(s"$name sending to manager")
      manager ! message
    }

    case unknown => log.warning(s"Unknown message: ${unknown.toString}")
  }

  override def preStart(): Unit = {
    manager ! RegisterClient(name, self)
  }

  override def postStop(): Unit = {
    manager ! UnregisterClient(name, self)
  }
}

object ClientTalkActor {
  def props(name: String, manager: ManagerService, out: ActorRef) =
    Props(classOf[ClientTalkActor], name, out, manager.getWorker)
}