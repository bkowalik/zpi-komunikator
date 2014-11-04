package actors

import play.api.libs.json.JsValue

trait DeserializeMessages {
  def deserialize(pf: PartialFunction[Any, Unit]): PartialFunction[Any, Unit] = {
    case json: JsValue => pf(json)
  }
}
