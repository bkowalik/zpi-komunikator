package actors

import java.security.MessageDigest
import java.util
import java.util.UUID

import actors.FileProtocol._
import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import com.sksamuel.diffpatch.DiffMatchPatch
import com.sksamuel.diffpatch.DiffMatchPatch.Patch
import org.joda.time.DateTime
import play.api.libs.json.{JsResult, JsValue, Reads}
import protocol.{EDated, Envelope}

class FileActor(val id: UUID, text: String, shadows: Map[Client, String] = Map.empty) extends Actor with ActorLogging {

  lazy val dmp = new DiffMatchPatch

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString
  }

  def receive = receiveWith(text, shadows)

  def receiveWith(text:String, shadows: Map[Client, String]): Receive = {
    case DiffFromClient(client, diff, checksum) =>
      val patch = dmp.patch_fromText(diff)
      val WTFWhoReturnsAnArrayOfObjects = dmp.patch_apply(new util.LinkedList[Patch](patch), text)
      val newText = WTFWhoReturnsAnArrayOfObjects(0).asInstanceOf[String]
      val changer = shadows.keys.filter(_.actor == client).head

      if (md5(newText) == checksum) {
        shadows.map(tpl => tpl._1.actor -> tpl._2) map { case (client1, shadow) =>
          val diff = dmp.patch_make(shadow, newText)
          if (client1 != client)
            client1 ! Diff(id, changer.username, dmp.patch_toText(diff))
          (client, newText)
        }
        context.become(receiveWith(newText, shadows))
      }
      else {
        log.warning(s"Patch $diff from $client produced invalid text.")
        log.warning(s"Sending server text to $client")
        client ! Text(text)
      }
    case AddClient(clients) =>
      val newShadow = clients.foldLeft(shadows) { (acc, client) =>
        acc.updated(client, text)
      }
      sender() ! AddClientAck(id, clients, text, shadows.keys)
      context.become(receiveWith(text, newShadow))
    case RemoveClientByName(clientName) =>
      context.become(receiveWith(text, shadows.filter(_._2 != clientName)))
    case RemoveClient(client) =>
      context.become(receiveWith(text, shadows - client))
    case GetText => sender() ! Text(id, text)
    case Participants => sender() ! ParticipantsList(shadows.keys)
    case unknown => log.error(s"Unknown message ${unknown.toString}")
  }

}

object FileActor {
  def props(id: UUID, text: String, shadows: Map[ActorRef, String] = Map.empty) =
    Props(classOf[FileActor], id, text, shadows)
}

sealed trait FileProtocol

object FileProtocol {
  case class DiffFromClient(client: ActorRef, diff: String, md5: String) extends FileProtocol

  case class AddClient(client: Set[Client]) extends FileProtocol
  case class AddClientAck(id: UUID, client: Set[Client], text: String, participants: Iterable[Client]) extends FileProtocol
  case class RemoveClientByName(client: String) extends FileProtocol
  case class RemoveClient(client: Client)
  case object GetText extends FileProtocol
  case object RemoveAll extends FileProtocol

  case object Participants
  case class ParticipantsList(participants: Iterable[Client])

  case class Diff(id: UUID, sender: String, text: String) extends FileProtocol
  case class Text(id: UUID, text: String) extends FileProtocol
}

case class Client(username: String, actor: ActorRef)