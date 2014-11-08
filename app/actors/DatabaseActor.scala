package actors

import actors.DatabaseProtocol.{RecoverMessage, StoreMessage}
import akka.actor.{ActorLogging, Props, Actor}
import protocol.TextMessage

class DatabaseActor extends Actor with ActorLogging {
  def receive = {
    case store: StoreMessage => log.debug(store.toString)
    case req: RecoverMessage => log.debug(req.toString)
    case _ => log.debug("Unrecognized message")
  }
}

object DatabaseActor {
  def props(): Props = Props(classOf[DatabaseActor])
}

sealed trait DatabaseProtocol
object DatabaseProtocol {
  case class StoreMessage(from: String, message: String) extends DatabaseProtocol
  case class RecoverMessage(to: String) extends DatabaseProtocol

  case class StoredMessages(to: String, messages: Iterable[TextMessage]) extends DatabaseProtocol
}
