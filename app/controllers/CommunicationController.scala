package controllers

import actors.ClientTalkActor
import akka.actor.ActorRef
import com.wordnik.swagger.annotations.{Api, ApiOperation}
import play.api.libs.json.JsValue
import play.api.mvc._
import services.ManagerService
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

@Api(value = "/", description = "Main operations on websocks")
class CommunicationController(managerService: ManagerService) extends Controller with Secured {
  import play.api.Play.current

  @ApiOperation(nickname = "clientChannel", value = "Clients connection", notes = "Clients WebSocket")
  def clientChannel(name: String) = WebSocket.tryAcceptWithActor[JsValue, JsValue] { request =>
    Future {
      username(request).map { username =>
        Right(ClientTalkActor.props(name, managerService, _: ActorRef))
      }.getOrElse(Left(Forbidden))
    }
  }
}