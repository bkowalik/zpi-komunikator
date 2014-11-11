package actors

import java.security.MessageDigest
import java.util

import actors.FileProtocol._
import akka.actor.{ActorRef, ActorLogging, Actor}
import com.sksamuel.diffpatch.DiffMatchPatch
import com.sksamuel.diffpatch.DiffMatchPatch.Patch

class FileActor(val text: String, val shadows: Map[ActorRef, String] = Map.empty) extends Actor with ActorLogging {

  lazy val dmp = new DiffMatchPatch

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString
  }

  def receive = receiveWith(text, shadows)

  def receiveWith(text:String, shadows: Map[ActorRef, String]): Receive = {
    case DiffFromClient(client, diff, checksum) =>
      val patch = dmp.patch_fromText(diff)
      val WTFWhoReturnsAnArrayOfObjects = dmp.patch_apply(new util.LinkedList[Patch](patch), text)
      val newText = WTFWhoReturnsAnArrayOfObjects(0).asInstanceOf[String]

      if (md5(newText) == checksum) {
        // TODO: should we send this to client that sent the diff?
        shadows foreach { case (client1, shadow) =>
          val diff = dmp.patch_make(shadow, newText)
          client1 ! Diff(dmp.patch_toText(diff))
        }
        context.become(receiveWith(newText, shadows))
      }
      else {
        log.warning(s"Patch $diff from $client produced invalid text.")
        log.warning(s"Sending server text to $client")
        client ! Text(text)
      }
    case AddClient(client) =>
      client ! Text(text)
      context.become(receiveWith(text, shadows.updated(client, text)))
    case RemoveClient(client) =>
      context.become(receiveWith(text, shadows - client))
    case GetText => sender() ! Text(text)
    case unknown => log.warning(s"Unknown message ${unknown.toString}")
  }

}

sealed trait FileProtocol

object FileProtocol {
  case class DiffFromClient(client: ActorRef, diff: String, md5: String) extends FileProtocol
  case class AddClient(client: ActorRef) extends FileProtocol
  case class RemoveClient(client: ActorRef) extends FileProtocol
  case object GetText extends FileProtocol

  case class Diff(text: String) extends FileProtocol
  case class Text(text: String) extends FileProtocol
}