package actors

import akka.actor.ActorRef
import akka.util.Timeout
import scala.concurrent.duration._

trait AskTimeout {
  implicit val timeout: Timeout = 2 seconds
}
