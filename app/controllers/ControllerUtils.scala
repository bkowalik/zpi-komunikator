package controllers

import scala.concurrent.duration._

trait ControllerUtils {
  implicit val timeout = 5 seconds
}
