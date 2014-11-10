package protocol

import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsArray, JsNull, JsString, Json}
import utils.DateTimeFormatters

class ProtocolTest extends FlatSpec with Matchers {

  it should "serialize text message with envelope" in {
    val textMessage = TextMessage("message")
    val dateTime = new DateTime()
    val envelope = new Envelope(Set("bar"), None, MessageTypes.TextMessageType, Json.toJson(textMessage)) with EDated with ESender {
      val date = dateTime
      val from = "foo"
    }

    val json = Json.toJson(envelope)

    (json \ "from") shouldBe JsString("foo")
    (json \ "to") shouldBe JsArray(Seq(JsString("bar")))
    (json \ "kind") shouldBe JsString("TextMessageType")
  }

  it should "serialize envelope with date without sender" in {
    val dateTime = new DateTime()
    val enveloper = new Envelope(Set("foo"), None, MessageTypes.TextMessageType, JsNull) with EDated {
      val date  = dateTime
    }

    val json = Json.toJson(enveloper)

    (json \\ "from") should be('empty)
    (json \ "date") shouldBe JsString(DateTimeFormatters.formatter.print(dateTime))
  }

  it should "deserialize text message in envelope" in {
    val jsonMessage = Json.obj(
      "message" -> JsString("message")
    )
    val json = Json.obj(
      "to" -> JsArray(Seq(JsString("bar"))),
      "kind" -> JsString("TextMessageType"),
      "payload" -> jsonMessage
    )

    val envelope = Json.fromJson[Envelope](json).get
    val message = Json.fromJson[TextMessage](envelope.payload).get

    envelope.to shouldBe Set("bar")
  }
}
