import actors.repo.UsersActor
import actors.{ClientsManager, DatabaseActor}
import com.softwaremill.macwire.MacwireMacros._
import controllers.{CommunicationController, UsersController}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import services.{UsersService, DatabaseService, ManagerService}

object Application {
  lazy val usersRepository = new UsersService(Akka.system.actorOf(UsersActor.props()))

  lazy val database = new DatabaseService(Akka.system.actorOf(DatabaseActor.props()))

  lazy val manager = new ManagerService(Akka.system.actorOf(ClientsManager.props(database)))

  lazy val communicationController = wire[CommunicationController]

  lazy val usersController = wire[UsersController]
}
