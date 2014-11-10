import java.util.UUID

import com.softwaremill.macwire.Macwire
import filters.CORSFilter
import models.{User, Users}
import play.api.GlobalSettings
import play.api.mvc.WithFilters
import utils.Application

object Global extends WithFilters(CORSFilter()) with GlobalSettings with Macwire {

  val wired = wiredInModule(Application)


  override def onStart(app: play.api.Application): Unit = {
    import scala.slick.driver.H2Driver.simple._
    val users = TableQuery[Users]
    utils.Application.database.withTransaction { implicit session =>
      users.ddl.create

      users.insert(User(Option(UUID.randomUUID()), "maniek", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.vKypt5LnoSyv3uxqWlWDpcxYVrY3bp6", "maniek@onet.pl"))
      users.insert(User(Option(UUID.randomUUID()), "zenek", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.L5E3r2VOihnZUf/K2ayujOwdB5mpVkq", "zenek@onet.pl"))
    }
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    wired.lookupSingleOrThrow(controllerClass)
  }
}
