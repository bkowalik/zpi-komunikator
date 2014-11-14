package actors.manager.handlers

import actors.ClientsManager
import play.api.libs.json.Json
import protocol._

trait TextMessageHandler {
  this: ClientsManager =>

  def textMessageHandler(msg: Envelope with ESender with EDated): Unit = {
    msg.to.filter(_ != msg.from).map { receiver =>
      clients.get(receiver).map { sockets =>
        sockets.foreach { actor =>
          log.debug("Receiver is connected, redirecting")
          actor ! msg
        }
      } getOrElse {
        log.debug("Receiver is not connected")
        val message = Json.fromJson[TextMessage](msg.payload).get
        //database ! StoreMessage(newMsg)
      }
    }
  }
}
