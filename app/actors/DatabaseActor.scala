package actors

import actors.DatabaseProtocol.{RecoverMessage, StoreMessage}
import akka.actor.{ActorLogging, Props, Actor}
import protocol.{Envelope, EDated, TextMessage}

class DatabaseActor extends Actor with ActorLogging {
  def receive = {
    case store: StoreMessage => log.debug(store.toString)
    case req: RecoverMessage => log.debug(req.toString)
    case unknown => log.warning(s"Unrecognized message: ${unknown.toString}")
  }
}

object DatabaseActor {
  def props(): Props = Props(classOf[DatabaseActor])
}

sealed trait DatabaseProtocol
object DatabaseProtocol {
  case class StoreMessage(envelope: Envelope with EDated) extends DatabaseProtocol
  case class RecoverMessage(to: String) extends DatabaseProtocol

  case class StoredMessages(to: String, messages: Iterable[TextMessage]) extends DatabaseProtocol
}
