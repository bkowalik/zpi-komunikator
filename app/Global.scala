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

      users.insert(User(Option(UUID.randomUUID()), "maniek", "maniek123", "maniek@onet.pl"))
      users.insert(User(Option(UUID.randomUUID()), "zenek", "zenek123", "zenek@onet.pl"))
    }
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    wired.lookupSingleOrThrow(controllerClass)
  }
}
