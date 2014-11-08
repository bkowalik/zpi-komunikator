package actors

import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import protocol.{Envelope, EnvelopeTimeStamp}

trait DeserializeMessages {
  def deserialize(pf: PartialFunction[Any, Unit]): PartialFunction[Any, Unit] = {
    case json: JsValue =>
      Json.fromJson[Envelope](json).map { msg =>
        val newMsg = new Envelope(msg.from, msg.to, msg.kind, msg.payload) with EnvelopeTimeStamp {
          val date = new DateTime()
        }
        pf(newMsg)
      }
  }
}
