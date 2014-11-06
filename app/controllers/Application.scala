package controllers

import actors.{ClientTalkActor, ClientsManager}
import akka.actor.Props
import com.wordnik.swagger.annotations.{ApiOperation, Api}
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.mvc._

@Api(value = "/", description = "Main operations on websocks")
object Application extends Controller {

  import play.api.Play.current

  val manager = Akka.system.actorOf(Props[ClientsManager])

  @ApiOperation(nickname = "clientChannel", value = "Clients connection", notes = "Clients WebSocket")
  def clientChannel(name: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    ClientTalkActor.props(name, out, manager)
  }
}