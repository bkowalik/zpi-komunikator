package protocol

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{JsString, JsArray, Json}

class RestProtocolTest extends FlatSpec with Matchers {

  behavior of "error message serializer"

  it should "serialize error message" in {
    val msg = FailureMessage(Map(
      "foo1" -> List("bar1"),
      "foo2" -> List("bar2a", "bar2b")
    ))

    val json = Json.toJson(msg)

    (json \ "foo1") shouldBe JsArray(Seq(JsString("bar1")))
    (json \ "foo2") shouldBe JsArray(Seq(JsString("bar2a"), JsString("bar2b")))
    (json \ "status") shouldBe JsString("ERROR")
  }

  behavior of "success message serializer"

  it should "serialize success message" in {
    val msg = SuccessMessage("super!")

    val json = Json.toJson(msg)

    (json \ "message") shouldBe JsString("super!")
    (json \ "status") shouldBe JsString("OK")
  }

}
