package protocol

import java.util.UUID

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json

class DiffSyncProtocolTest extends FlatSpec with Matchers {

  behavior of "NewSession serializer"

  it should "serialize NewSession" in {
    val newSession = NewSession("bar")

    val json = Json.toJson(newSession)

    (json \ "kind").as[String] should equal("NewSession")
    (json \ "text").as[String] should equal("bar")
  }

  it should "deserialize NewSession" in {
    val newSessionJson = Json.obj(
      "kind" -> "NewSession",
      "text" -> "bar"
    )

    val obj = Json.fromJson[DiffSync](newSessionJson)

    obj.get shouldBe a[NewSession]
    obj.get.asInstanceOf[NewSession].text shouldBe "bar"
  }

  behavior of "CloseSession serializer"

  it should "serialize CloseSession" in {
    val uuid = UUID.randomUUID()
    val closeSession = CloseSession(uuid)

    val json = Json.toJson(closeSession)

    (json \ "kind").as[String] should equal("CloseSession")
    (json \ "id").as[UUID] should equal(uuid)
  }

  it should "deserialize CloseSession" in {
    val uuid = UUID.randomUUID()
    val json = Json.obj(
      "kind" -> "CloseSession"
    )

    val obj = Json.fromJson[DiffSync](json).get

    obj shouldBe a[CloseSession]
  }

  behavior of "AddUser serializer"

  it should "serialize AddUser" in {
    val addUser = AddUser("foo")

    val json = Json.toJson(addUser)

    (json \ "kind").as[String] should equal("AddUser")
    (json \ "username").as[String] should equal("foo")
  }

  it should "deserialize AddUser" in {
    val json = Json.obj(
      "kind" -> "AddUser",
      "username" -> "foo"
    )

    val obj = Json.fromJson[DiffSync](json).get

    obj shouldBe a[AddUser]
    obj.asInstanceOf[AddUser].username should equal("foo")
  }

  behavior of "RemoveUser serializer"

  it should "serialize RemoveUser" in {
    val removeUser = RemoveUser("foo")

    val json = Json.toJson(removeUser)

    (json \ "kind").as[String] should equal("RemoveUser")
    (json \ "username").as[String] should equal("foo")
  }

  it should "deserialize RemoveUser" in {
    val json = Json.obj(
      "kind" -> "RemoveUser",
      "username" -> "foo"
    )

    val obj = Json.fromJson[DiffSync](json).get

    obj shouldBe a[RemoveUser]
    obj.asInstanceOf[RemoveUser].username should equal("foo")
  }

  behavior of "Diff serializer"

  it should "serialize Diff" in {
    val diff = Diff("foo")

    val json = Json.toJson(diff)

    (json \ "kind").as[String] should equal("Diff")
    (json \ "diff").as[String] should equal("foo")

  }

  it should "deserialize Diff" in {
    val jsonDiff = Json.obj(
      "kind" -> "Diff",
      "diff" -> "foo",
      "md5" -> "abc"
    )

    val patch = Json.fromJson[DiffSync](jsonDiff)

    patch.get shouldBe a[Diff]
    patch.get.asInstanceOf[Diff].text shouldBe "foo"
  }

  behavior of "Text serializer"

  it should "serialize Text" in {
    val text = Text("foo")

    val json = Json.toJson(text)

    (json \ "kind").as[String] should equal("Text")
    (json \ "text").as[String] should equal("foo")
  }
}
