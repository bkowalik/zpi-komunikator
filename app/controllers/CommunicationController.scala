package controllers

import actors.ClientTalkActor
import akka.actor.ActorRef
import com.wordnik.swagger.annotations.{Api, ApiOperation}
import play.api.libs.json.JsValue
import play.api.mvc._
import services.ManagerService

@Api(value = "/", description = "Main operations on websocks")
class CommunicationController(managerService: ManagerService) extends Controller {
  import play.api.Play.current
  import managerService.manager

  @ApiOperation(nickname = "clientChannel", value = "Clients connection", notes = "Clients WebSocket")
  def clientChannel(name: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    ClientTalkActor.props(name, out, manager)
  }
}