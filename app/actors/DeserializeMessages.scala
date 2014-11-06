package actors

import play.api.libs.json.{Json, JsValue}
import protocol.{TextMessage, Envelope}
import protocol.MessageTypes._

trait DeserializeMessages {
  def deserialize(pf: PartialFunction[Any, Unit]): PartialFunction[Any, Unit] = {
    case json: JsValue =>
      Json.fromJson[Envelope](json).map(pf)
  }
}
