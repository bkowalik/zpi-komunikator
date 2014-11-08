import com.softwaremill.macwire.Macwire
import filters.CORSFilter
import play.api.GlobalSettings
import play.api.mvc.WithFilters
import com.softwaremill.macwire.MacwireMacros._

object Global extends WithFilters(CORSFilter()) with GlobalSettings with Macwire {

  val wired = wiredInModule(Application)

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    wired.lookupSingleOrThrow(controllerClass)
  }
}
