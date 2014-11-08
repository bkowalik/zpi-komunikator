package actors

import play.api.libs.json.{JsValue, Json}
import protocol.Envelope

trait DeserializeMessages {
  def deserialize(pf: PartialFunction[Any, Unit]): PartialFunction[Any, Unit] = {
    case json: JsValue => Json.fromJson[Envelope](json).map(pf)
    case msg => pf(msg)
  }
}
