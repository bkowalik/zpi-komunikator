package protocol

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json._
import protocol.MessageTypes.MessageType
import utils.{DateTimeFormatters, EnumUtils}

case class Envelope(to: Set[String], uuid: Option[UUID], kind: MessageType, payload: JsValue)

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
case class NewSession(text: String) extends DiffSync
object NewSession {
  implicit val reads = Json.reads[NewSession]
  implicit val writes = new Writes[NewSession] {
    def writes(obj: NewSession): JsValue = {
      Json.obj(
        "kind" -> "NewSession",
        "text" -> obj.text
      )
    }
  }
}
case class CloseSession() extends DiffSync
object CloseSession {
  implicit val reads = new Reads[CloseSession] {
    def reads(json: JsValue): JsResult[CloseSession] = {
      JsSuccess(CloseSession())
    }
  }
  implicit val writes = new Writes[CloseSession] {
    def writes(obj: CloseSession): JsValue = {
      Json.obj(
        "kind" -> "CloseSession"
      )
    }
  }
}
case class AddUser(username: String) extends DiffSync
object AddUser {
  implicit val reads = Json.reads[AddUser]
  implicit val writes = new Writes[AddUser] {
    def writes(o: AddUser): JsValue = {
      Json.obj(
        "kind" -> "AddUser",
        "username" -> o.username
      )
    }
  }
}
case class RemoveUser(username: String) extends DiffSync
object RemoveUser {
  implicit val reads = Json.reads[RemoveUser]
  implicit val writes = new Writes[RemoveUser] {
    def writes(o: RemoveUser): JsValue = {
      Json.obj(
        "kind" -> "RemoveUser",
        "username" -> o.username
      )
    }
  }
}
case class Text(text: String) extends DiffSync
object Text {
  implicit val writes = new Writes[Text] {
    def writes(obj: Text): JsValue = {
      Json.obj(
        "kind" -> obj.getClass.getSimpleName,
        "text" -> obj.text
      )
    }
  }
}
case class Diff(text: String) extends DiffSync
trait CheckSumMD5 {
  this: Diff =>
  val md5: String
}
object Diff {
  implicit val formatter = new Format[Diff] {
    def reads(json: JsValue): JsResult[Diff] = {
      val md5Str = (json \ "md5").as[String]
      val text = (json \ "diff").as[String]

      JsSuccess(new Diff(text) with CheckSumMD5 {
        val md5: String = md5Str
      })
    }

    def writes(o: Diff): JsValue = {
      val md5 = o match {
        case a: CheckSumMD5 => Option(a.md5)
        case _ => None
      }

      JsObject(
        md5.map(m => "md5" -> JsString(m)).toSeq ++ Seq("diff" -> JsString(o.text), "kind" -> JsString(o.getClass.getSimpleName))
      )
    }
  }
}
object DiffSync {
  implicit val reads = new Reads[DiffSync] {
    def reads(json: JsValue): JsResult[DiffSync] = {
      (json \ "kind").as[String] match {
        case "NewSession" => Json.fromJson[NewSession](json)
        case "CloseSession" => Json.fromJson[CloseSession](json)
        case "Diff" => Json.fromJson[Diff](json)
        case "AddUser" => Json.fromJson[AddUser](json)
        case "RemoveUser" => Json.fromJson[RemoveUser](json)
      }
    }
  }
}

object Envelope {
  implicit val reads = new Reads[Envelope] {
    def reads(json: JsValue): JsResult[Envelope] = {
      val to = (json \ "to").asOpt[Set[String]].getOrElse(Set.empty)
      val uuid = (json \ "id").asOpt[UUID]
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

  val AudiVideoType = Value("AudioVideoType")

  implicit val enumReads: Reads[MessageType] = EnumUtils.enumReads(MessageTypes)

  implicit def enumWrites: Writes[MessageType] = EnumUtils.enumWrites
}