package controllers

import org.mockito.Mockito._
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import util.BaseControllerTest
import utils.Application

class UsersControllerTest extends BaseControllerTest {

  it should "return forbidden when calling logout" in {
    val request = FakeRequest(GET, "/users/logout")

    val result = route(request).get

    status(result) shouldBe UNAUTHORIZED
  }

  val registerJson = Json.obj(
    "username" -> "foo",
    "email" -> "abc@def.pl"
  )
  
  it should "reject register with short password" in {
    val withPassword = registerJson + ("password" -> JsString("foo"))
    val request = FakeRequest(POST, "/users/register").withJsonBody(withPassword)

    val result = route(request).get

    status(result) shouldBe BAD_REQUEST
  }

  it should "register user" in {
    val withPassword = registerJson + ("password" -> JsString("foo123"))
    val request = FakeRequest(POST, "/users/register").withJsonBody(withPassword)

    val result = route(request).get

    status(result) shouldBe OK
    contentAsJson(result) shouldBe Json.obj("status" -> "OK", "message" -> "Account created")
  }
}
