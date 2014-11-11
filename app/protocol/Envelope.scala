package protocol

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json._
import protocol.MessageTypes.MessageType
import utils.{DateTimeFormatters, EnumUtils}

case class Envelope(to: Set[String], uuid: Option[String], kind: MessageType, payload: JsValue)

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

sealed trait DiffSync
object DiffSync {
  implicit val reads = new Reads[DiffSync] {
    def reads(json: JsValue): JsResult[DiffSync] = {
      val text = (json \ "text").as[String]
      val obj = (json \ "kind").as[String] match {
        case "NewSession" => NewSession(text)
        case "Patch" => Patch(text)
      }

      JsSuccess(obj)
    }
  }

  implicit val writes = new Writes[DiffSync] {
    def writes(o: DiffSync): JsValue = o match {
      case msg @ NewSession(text) => Json.obj(
        "kind" -> "NewSession",
        "text" -> text
      )
      case msg @ Patch(text) => Json.obj(
        "kind" -> "Patch",
        "text" -> text
      )
    }
  }
}
case class NewSession(text: String) extends DiffSync
case class Patch(text: String) extends DiffSync

object Envelope {
  implicit val reads = new Reads[Envelope] {
    def reads(json: JsValue): JsResult[Envelope] = {
      val to = (json \ "to").asOpt[Set[String]].getOrElse(Set.empty)
      val uuid = (json \ "id").asOpt[String]
      val kind = (json \ "kind").as[MessageType]
      val payload = json \ "payload"

      JsSuccess(Envelope(to, uuid, kind, payload))
    }
  }

  implicit val writes = new Writes[Envelope with EDated] {
    def writes(env: Envelope with EDated): JsValue = {
      val sender = env match {
        case a: ESender => Some(a.from)
        case _ => None
      }
      JsObject(
        sender.map(from => "from" -> JsString(from)).toSeq ++
        env.uuid.map(id => "id" -> Json.toJson(id)).toSeq ++
        Seq(
          "to" -> JsArray(env.to.map(JsString).toSeq),
          "date" -> JsString(DateTimeFormatters.formatter.print(env.date)),
          "kind" -> Json.toJson(env.kind),
          "payload" -> env.payload
        )
      )
    }
  }

  def keepAliveMessage(client: String) = new Envelope(Set(client), None, MessageTypes.KeepAlive, JsNull) with EDated {
    val date: DateTime = new DateTime()
  }
}

object MessageTypes extends Enumeration {
  type MessageType = Value

  val TextMessageType = Value("TextMessageType")

  val UserLoggedInType = Value("UserLoggedInType")

  val UserLoggedOutType = Value("UserLoggedOutType")

  val DiffSyncType = Value("DiffSyncType")

  val KeepAlive = Value("KeepAlive")

  implicit val enumReads: Reads[MessageType] = EnumUtils.enumReads(MessageTypes)

  implicit def enumWrites: Writes[MessageType] = EnumUtils.enumWrites
}