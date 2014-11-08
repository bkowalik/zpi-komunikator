package actors

import play.api.libs.json.{JsValue, Json}
import protocol.{ESender, Envelope}

trait DeserializeMessages {
  this: ClientTalkActor =>

  def deserialize(pf: PartialFunction[Any, Unit]): PartialFunction[Any, Unit] = {
    case json: JsValue => Json.fromJson[Envelope](json).map { env =>
      new Envelope(env.to, env.kind, env.payload) with ESender {
        val from: String = name
      }
    }
    case msg => pf(msg)
  }
}
