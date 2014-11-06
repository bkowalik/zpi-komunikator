package protocol

import org.joda.time.DateTime
import play.api.libs.json._
import protocol.MessageTypes.MessageType
import utils.{DateTimeFormatters, EnumUtils}

case class Envelope(from: Option[String], to: Option[String], date: DateTime, kind: MessageType, payload: JsValue)

case class TextMessage(message: String)
object TextMessage {
  implicit val formatter = Json.format[TextMessage]
}

object Envelope {
  implicit val formatter = new Format[Envelope] {
    def reads(json: JsValue): JsResult[Envelope] = {
      val from = (json \ "from").asOpt[String]
      val to = (json \ "to").asOpt[String]
      val date = (json \ "date").as[String]
      val kind = (json \ "kind").as[MessageType]
      val payload = json \ "payload"

      JsSuccess(Envelope(from, to, DateTimeFormatters.formatter.parseDateTime(date), kind, payload))
    }

    def writes(env: Envelope): JsValue = {
      val from = env.from.map(str => "from" -> JsString(str))
      val to = env.to.map(str => "to" -> JsString(str))
      JsObject((from :: to :: Nil).flatten ++ Seq(
        "date" -> JsString(DateTimeFormatters.formatter.print(env.date)),
        "kind" -> Json.toJson(env.kind),
        "payload" -> env.payload
      ))
    }
  }
}

object MessageTypes extends Enumeration {
  type MessageType = Value

  val TextMessageType = Value("TextMessageType")

  implicit val enumReads: Reads[MessageType] = EnumUtils.enumReads(MessageTypes)

  implicit def enumWrites: Writes[MessageType] = EnumUtils.enumWrites
}