package actors

import akka.actor.{ActorRef, Actor}

class ClientsManager extends Actor {

  var clients = Map.empty[String, ActorRef]

  def receive = ???
}

object ClientsManager {
  case class Client(name: String, channel: ActorRef)
}

sealed trait ManagerProtocol
object ManagerProtocol {
  case class RegisterClient() extends ManagerProtocol
  case class UnregisterClient() extends ManagerProtocol
}

