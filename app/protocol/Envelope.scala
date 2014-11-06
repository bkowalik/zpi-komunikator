package protocol

import play.api.libs.json.{Writes, Reads, JsValue, Json}
import protocol.MessageTypes.MessageType
import utils.EnumUtils

case class Envelope(from: String, to: Option[String], kind: MessageType, payload: JsValue)

case class TextMessage(message: String)
object TextMessage {
  implicit val formatter = Json.format[TextMessage]
}

object Envelope {
  implicit val formatter = Json.format[Envelope]
}

object MessageTypes extends Enumeration {
  type MessageType = Value

  val TextMessageType = Value("TextMessageType")

  implicit val enumReads: Reads[MessageType] = EnumUtils.enumReads(MessageTypes)

  implicit def enumWrites: Writes[MessageType] = EnumUtils.enumWrites
}