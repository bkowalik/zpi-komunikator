package protocol

import java.util.UUID

import play.api.libs.json.Json

case class TextMessage(source: String, destination: UUID, message: String)

object TextMessage {
  implicit val formatter = Json.format[TextMessage]
}
