package protocol

import org.joda.time.DateTime
import play.api.libs.json._
import protocol.MessageTypes.MessageType
import utils.{DateTimeFormatters, EnumUtils}

case class Envelope(to: Set[String], kind: MessageType, payload: JsValue)

trait EDated {
  this: Envelope =>
  val date: DateTime
}

trait ESender {
  this: Envelope =>
  val from: String
}


case class TextMessage(message: String)
object TextMessage {
  implicit val formatter = Json.format[TextMessage]
}

case class UserLoggedIn(username: String)
object UserLoggedIn {
  implicit val writes = Json.writes[UserLoggedIn]
}

case class UserLoggedOut(username: String)
object UserLoggedOut {
  implicit val writes = Json.writes[UserLoggedOut]
}

object Envelope {
  implicit val reads = new Reads[Envelope] {
    def reads(json: JsValue): JsResult[Envelope] = {
      val to = (json \ "to").asOpt[Set[String]].getOrElse(Set.empty)
      val kind = (json \ "kind").as[MessageType]
      val payload = json \ "payload"

      JsSuccess(Envelope(to, kind, payload))
    }
  }

  implicit val writes = new Writes[Envelope with EDated] {
    def writes(env: Envelope with EDated): JsValue = {
      val sender = env match {
        case a: ESender => Some(a.from)
        case _ => None
      }
      JsObject(sender.map(from => "from" -> JsString(from)).toSeq ++ Seq(
        "to" -> JsArray(env.to.map(JsString).toSeq),
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

  val UserLoggedInType = Value("UserLoggedInType")

  val UserLoggedOutType = Value("UserLoggedOutType")

  implicit val enumReads: Reads[MessageType] = EnumUtils.enumReads(MessageTypes)

  implicit def enumWrites: Writes[MessageType] = EnumUtils.enumWrites
}