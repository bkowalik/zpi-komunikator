package services

import akka.util.Timeout

import scala.concurrent.duration._

trait AsyncService {
  implicit val timeout: Timeout = 5 seconds
}
