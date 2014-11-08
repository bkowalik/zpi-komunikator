package protocol

import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsNull, JsString, Json}
import utils.DateTimeFormatters

class ProtocolTest extends FlatSpec with Matchers {

  it should "serialize text message with envelope" in {
    val textMessage = TextMessage("message")
    val dateTime = new DateTime()
    val envelope = new Envelope(Option("foo"), Option("bar"), MessageTypes.TextMessageType, Json.toJson(textMessage)) with EnvelopeTimeStamp {
      val date = dateTime
    }

    val json = Json.toJson(envelope)

    (json \ "from") shouldBe JsString("foo")
    (json \ "to") shouldBe JsString("bar")
    (json \ "kind") shouldBe JsString("TextMessageType")
  }

  it should "serialize envelope with date" in {
    val dateTime = new DateTime()
    val enveloper = new Envelope(Option("foo"), Option("bar"), MessageTypes.TextMessageType, JsNull) with EnvelopeTimeStamp {
      val date  = dateTime
    }

    val json = Json.toJson(enveloper)

    (json \ "date") shouldBe JsString(DateTimeFormatters.formatter.print(dateTime))
  }

  it should "deserialize text message in envelope" in {
    val jsonMessage = Json.obj(
      "message" -> JsString("message")
    )
    val json = Json.obj(
      "from" -> JsString("foo"),
      "to" -> JsString("bar"),
      "kind" -> JsString("TextMessageType"),
      "payload" -> jsonMessage
    )

    val envelope = Json.fromJson[Envelope](json).get
    val message = Json.fromJson[TextMessage](envelope.payload).get

    envelope.from shouldBe Some("foo")
    envelope.to shouldBe Some("bar")
  }

  ignore should "deserialize message to server" in {
    val json = Json.obj(
      "from" -> JsString("foo"),
      "kind" -> ""
    )
  }
}
