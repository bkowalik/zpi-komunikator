package protocol

import play.api.data.FormError
import play.api.libs.json._
import protocol.RestProtocol.Status

sealed trait RestProtocol {
  val status: RestProtocol.Status
}
object RestProtocol {
  sealed trait Status
  case object OK extends Status
  case object ERROR extends Status
}

case class SuccessMessage(message: String) extends RestProtocol {
  override val status: Status = RestProtocol.OK
}
object SuccessMessage {
  implicit val writes = new Writes[SuccessMessage] {
    def writes(msg: SuccessMessage): JsValue = Json.obj(
      "status" -> msg.status.toString,
      "message" -> msg.message
    )
  }
}

case class FailureMessage protected[protocol](errors: Map[String, Iterable[String]]) extends RestProtocol {
  override val status: Status = RestProtocol.ERROR
}
object FailureMessage {
  def apply(errors: Iterable[FormError]): FailureMessage = {
    val strErr = errors.map { formError =>
      formError.key -> formError.messages
    }.toMap

    FailureMessage(strErr)
  }

  implicit val writes = new Writes[FailureMessage] {
    def writes(msg: FailureMessage): JsValue = JsObject(
      (msg.errors.map {
        case (field, errors) => field -> JsArray(errors.map(JsString).toSeq)
      } + ("status" -> JsString(msg.status.toString))).toSeq
    )
  }
}