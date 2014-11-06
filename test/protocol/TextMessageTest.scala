package protocol

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json._

class TextMessageTest extends FlatSpec with Matchers {

  it should "serialize text message" in {
    val message = TextMessage("text message")

    val json = Json.toJson(message)

    (json \ "message") shouldBe JsString("text message")
  }

  it should "deserialize text message" in {
    val serialized = JsObject(Seq(
      "message" -> JsString("text message")
    ))

    val obj = Json.fromJson[TextMessage](serialized).get

    obj.message shouldBe "text message"
  }

}

