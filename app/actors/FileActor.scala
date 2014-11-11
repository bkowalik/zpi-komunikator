package actors

import actors.FileProtocol._
import akka.actor.{ActorRef, ActorLogging, Actor}
import com.sksamuel.diffpatch.DiffMatchPatch

class FileActor(var text: String, var shadows: Map[ActorRef, String] = Map.empty) extends Actor with ActorLogging {

  lazy val dmp = new DiffMatchPatch

  def receive = {
    case DiffFromClient(client, diff) => ???
    case AddClient(client) =>
      client ! Text(text)
      shadows = shadows.updated(client, text)
    case RemoveClient(client) =>
      shadows = shadows - client
    case GetText => sender() ! Text(text)
    case unknown => log.warning(s"Unknown message ${unknown.toString}")
  }

}

sealed trait FileProtocol

object FileProtocol {
  case class DiffFromClient(client: ActorRef, diff: String) extends FileProtocol
  case class AddClient(client: ActorRef) extends FileProtocol
  case class RemoveClient(client: ActorRef) extends FileProtocol
  case object GetText extends FileProtocol

  case class Diff(text: String) extends FileProtocol
  case class Text(text: String) extends FileProtocol
}