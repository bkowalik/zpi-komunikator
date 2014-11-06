package protocol

import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsString, Json}
import utils.DateTimeFormatters

class ProtocolTest extends FlatSpec with Matchers {

  it should "serialize text message with envelope" in {
    val textMessage = TextMessage("message")
    val dateTime = new DateTime()
    val envelope = Envelope(Option("foo"), Option("bar"), dateTime, MessageTypes.TextMessageType, Json.toJson(textMessage))

    val json = Json.toJson(envelope)

    (json \ "from") shouldBe JsString("foo")
    (json \ "to") shouldBe JsString("bar")
    (json \ "kind") shouldBe JsString("TextMessageType")
  }

  it should "deserialize text message in envelope" in {
    val jsonMessage = Json.obj(
      "message" -> JsString("message")
    )
    val json = Json.obj(
      "from" -> JsString("foo"),
      "to" -> JsString("bar"),
      "date" -> JsString(DateTimeFormatters.formatter.print(new DateTime())),
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
