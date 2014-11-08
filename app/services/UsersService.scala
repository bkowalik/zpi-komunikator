package services

import actors.repo.UsersProtocol
import actors.repo.UsersProtocol.Login
import akka.actor.ActorRef
import akka.pattern.ask

import scala.concurrent.Future

class UsersService(usersActor: ActorRef) extends AsyncService {
  def login(username: String, password: String): Future[UsersProtocol] = {
    usersActor.ask(Login(username, password)).asInstanceOf[Future[UsersProtocol]]
  }
}
