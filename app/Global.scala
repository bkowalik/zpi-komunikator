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

      users.insert(User(Option(UUID.randomUUID()), "piotrek", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.AYc4W87/Yq20QpQUJH.0JJ98RwJ5SKa", ""))
      users.insert(User(Option(UUID.randomUUID()), "olek", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.crNAbruVT8ZqDk7AGyx2u4Br.nwKX6O", ""))
      users.insert(User(Option(UUID.randomUUID()), "andrzej", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.Ajw4YcfYnw1e1qZAr/RaruL5p3DIr9G", ""))
      users.insert(User(Option(UUID.randomUUID()), "dawid", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.6SURv1OU3iJ9oDiI7Gl4I6JoAhgH7Qm", ""))
      users.insert(User(Option(UUID.randomUUID()), "mateusz", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.HnjD29O/iuIKS.zVTmJAdFJQD4cz//i", ""))
      users.insert(User(Option(UUID.randomUUID()), "florek", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.NnVJxeOLUrGt3/27NeNIGLUwQy.icTK", ""))
      users.insert(User(Option(UUID.randomUUID()), "bartek", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.Bvr3wY6HMnqZOEjegbrCFCj0ETQtPcy", ""))
      users.insert(User(Option(UUID.randomUUID()), "marcin", "$2a$10$BPHHd7ed0HX5j9MF7zjrh.XDv46KpgpyOgg3Rt7XRGWa4.ZCdZhnq", ""))
    }
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    wired.lookupSingleOrThrow(controllerClass)
  }
}
