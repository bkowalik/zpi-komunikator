package protocol

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class DiffSyncProtocolTest extends FlatSpec with Matchers {

  val patch: DiffSync = Patch("foo")

  val newSession: DiffSync = NewSession("bar")

  val patchJson = Json.obj(
    "kind" -> "Patch",
    "text" -> "foo"
  )

  val newSessionJson = Json.obj(
    "kind" -> "NewSession",
    "text" -> "bar"
  )

  it should "serialize based on type" in {
    val pj = Json.toJson(patch)
    (pj \ "kind").as[String] should equal("Patch")
    (pj \ "text").as[String] should equal("foo")

    val nsj = Json.toJson(newSession)
    (nsj \ "kind").as[String] should equal("NewSession")
    (nsj \ "text").as[String] should equal("bar")
  }

  it should "deserialize base on kind" in {
    val patch = Json.fromJson[DiffSync](patchJson)
    patch.get shouldBe a[Patch]
    patch.get.asInstanceOf[Patch].text shouldBe "foo"

    val newSession = Json.fromJson[DiffSync](newSessionJson)
    newSession.get shouldBe a[NewSession]
    newSession.get.asInstanceOf[NewSession].text shouldBe "bar"
  }
}
