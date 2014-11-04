package controllers

import actors.{ClientTalkActor, ClientsManager}
import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.mvc._

object Application extends Controller {

  val manager = Akka.system.actorOf(Props[ClientsManager])

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def clientChannel = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    ClientTalkActor.props(out, manager)
  }
}