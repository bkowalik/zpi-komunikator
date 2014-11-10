package utils

import actors.ClientsManager
import actors.repo.UsersActor
import com.softwaremill.macwire.MacwireMacros._
import controllers.{CommunicationController, UsersController}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import repositories.UsersRepository
import services.{ManagerService, UsersService}

object Application extends Database with Other {
  lazy val usersRepository: UsersRepository = wire[UsersRepository]

  lazy val usersService = new UsersService(Akka.system.actorOf(UsersActor.props(usersRepository, appSalt)))

  lazy val manager = new ManagerService(Akka.system.actorOf(ClientsManager.props()))

  lazy val communicationController = wire[CommunicationController]

  lazy val usersController = wire[UsersController]
}
