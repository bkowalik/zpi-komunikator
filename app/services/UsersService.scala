package services

import actors.repo.UsersProtocol
import actors.repo.UsersProtocol.{UserCreationStatus, CreateUser, LoginStatus, Login}
import akka.actor.ActorRef
import akka.pattern.ask

import scala.concurrent.Future

class UsersService(usersActor: ActorRef) extends AsyncService {
  def login(username: String, password: String): Future[LoginStatus] = {
    usersActor.ask(Login(username, password)).asInstanceOf[Future[LoginStatus]]
  }

  def createUser(username: String, password: String, email: String) = {
    usersActor.ask(CreateUser(username, password, email)).asInstanceOf[UserCreationStatus]
  }
}
