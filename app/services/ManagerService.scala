package services

import actors.ManagerProtocol.{GiveAllOnline, FriendsList, CheckFriendsAvailability}
import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._

class ManagerService(manager: ActorRef) extends AsyncService {

  def checkFriends(friends: Iterable[String]): Future[FriendsList] = {
    manager.ask(CheckFriendsAvailability(friends)).asInstanceOf[Future[FriendsList]]
  }

  def checkAllOnline(): Future[Iterable[String]] = {
    manager.ask(GiveAllOnline).asInstanceOf[Future[Iterable[String]]]
  }

  def getWorker: ActorRef = manager
}
