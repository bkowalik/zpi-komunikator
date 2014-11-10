package util

import akka.util.Timeout
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, FlatSpec}
import play.api.Play
import play.api.http.{Status, HeaderNames}
import play.api.mvc.Results
import play.api.test._
import scala.concurrent.duration._

abstract class BaseControllerTest extends FlatSpec
  with Matchers
  with MockitoSugar
  with Results
  with PlayRunners
  with Writeables
  with RouteInvokers
  with HeaderNames
  with Status
  with ResultExtractors {

  implicit val app = Application.app

  implicit val timeout: Timeout = 5 seconds
}

object Application {
  lazy val app: FakeApplication = {
    val localApp = FakeApplication(additionalConfiguration = Map.empty)
    Play.start(localApp)
    localApp
  }
}