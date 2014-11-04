package protocol

import java.util.UUID

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json._

class TextMessageTest extends FlatSpec with Matchers {



  it should "serialize text message" in {
    val message = TextMessage("foo", UUID.randomUUID(), "bazz")

    val json = Json.toJson(message)

    (json \ "source").as[String] should equal("foo")
  }

}

