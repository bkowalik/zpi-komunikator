package services

import actors.ManagerProtocol.{FriendsList, CheckFriendsAvailability}
import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._

class ManagerService(val manager: ActorRef) {

  implicit val timeout: Timeout = 5 seconds

  def checkFriends(friends: Iterable[String]): Future[FriendsList] = {
    manager.ask(CheckFriendsAvailability(friends)).asInstanceOf[Future[FriendsList]]
  }
}
