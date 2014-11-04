package controllers

import actors.{ClientTalkActor, ClientsManager}
import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.mvc._

object Application extends Controller {

  import play.api.Play.current

  val manager = Akka.system.actorOf(Props[ClientsManager])

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def clientChannel(name: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    ClientTalkActor.props(name, out, manager)
  }
}