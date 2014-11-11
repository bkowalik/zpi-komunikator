package services

import actors.repo.UsersProtocol._
import akka.actor.ActorRef
import akka.pattern.ask

import scala.concurrent.Future

class UsersService(usersActor: ActorRef) extends AsyncService {
  def login(username: String, password: String): Future[LoginStatus] = {
    usersActor.ask(Login(username, password)).asInstanceOf[Future[LoginStatus]]
  }

  def createUser(username: String, password: String, email: String): Future[UserCreationStatus] = {
    usersActor.ask(CreateUser(username, password, email)).asInstanceOf[Future[UserCreationStatus]]
  }

  def allUsers: Future[Iterable[String]] = {
    usersActor.ask(AllUsers).asInstanceOf[Future[Iterable[String]]]
  }

  def changePassword(username: String, oldPassword: String, newPassword: String): Future[ChangePasswordStatus] = {
    usersActor.ask(ChangePassword(username, oldPassword, newPassword)).asInstanceOf[Future[ChangePasswordStatus]]
  }
}
