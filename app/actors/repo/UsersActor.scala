package actors.repo

import akka.actor.{Props, Actor, ActorLogging}
import models.Users
import com.github.t3hnar.bcrypt._
import repositories.UsersRepository
import scala.concurrent.Future
import akka.pattern.pipe

class UsersActor(usersRepository: UsersRepository, salt: String) extends Actor with ActorLogging{
  import UsersProtocol._
  import context.dispatcher

  def receive: Receive = {
    case Login(username, password) => login(username, password).map {
      case true => LoginSuccessful
      case false => LoginFailure
    }.pipeTo(sender())

    case CreateUser(username, password, email) => ???
  }

  def login(username: String, password: String): Future[Boolean] = Future {
    usersRepository.findByUsername(username)
  }.map(_.exists(usr => password.isBcrypted(usr.password)))
}

object UsersActor {
  def props(usersRepository: UsersRepository, salt: String): Props = {
    Props(classOf[UsersActor], usersRepository, salt)
  }
}

sealed trait UsersProtocol
object UsersProtocol {
  case class Login(username: String, password: String) extends UsersProtocol
  case class CreateUser(username: String, password: String, email: String) extends UsersProtocol

  sealed trait UserCreationStatus
  case object UserCreationSuccess extends UserCreationStatus
  case object UserCreationFailure extends UserCreationStatus

  sealed trait LoginStatus extends UsersProtocol
  case object LoginSuccessful extends LoginStatus
  case object LoginFailure extends LoginStatus
}