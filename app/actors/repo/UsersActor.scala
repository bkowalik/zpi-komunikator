package actors.repo

import akka.actor.{Props, Actor, ActorLogging}

class UsersActor extends Actor with ActorLogging{
  import UsersProtocol._

  def receive: Receive = {
    case Login(username, password) => sender() ! (if(username == password) LoginSuccessful else LoginFailure)
  }
}

object UsersActor {
  def props(): Props = {
    Props(classOf[UsersActor])
  }
}

sealed trait UsersProtocol
object UsersProtocol {
  case class Login(username: String, password: String) extends UsersProtocol

  case object LoginSuccessful extends UsersProtocol
  case object LoginFailure extends UsersProtocol
}