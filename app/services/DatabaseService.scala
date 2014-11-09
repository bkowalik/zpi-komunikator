package services

import akka.actor.ActorRef

class DatabaseService(val database: ActorRef) extends AsyncService {

}
