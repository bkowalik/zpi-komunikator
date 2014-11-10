package controllers

import org.mockito.Mockito._
import play.api.test.FakeRequest
import util.BaseControllerTest

class UsersControllerTest extends BaseControllerTest {

  it should "return forbidden when calling logout" in {
    val request = FakeRequest(GET, "/users/logout")

    val result = route(request).get

    status(result) shouldBe UNAUTHORIZED
  }
}
