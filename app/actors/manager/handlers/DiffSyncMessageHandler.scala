package actors.manager.handlers

import java.util.UUID

import actors._
import akka.actor.PoisonPill
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import protocol._
import akka.pattern.ask

trait DiffSyncMessageHandler extends AskTimeout {
  this: ClientsManager =>

  import context.dispatcher

  def diffSyncHandler(msg: Envelope with ESender with EDated): Unit = {
    Json.fromJson[DiffSync](msg.payload).get match {
      case NewSession(text) => {
        val id = UUID.randomUUID()
        val shadows = clients(msg.from).map(client => client -> text).toMap
        val fileActor = context.system.actorOf(FileActor.props(id, text, shadows))

        diffSyncs = diffSyncs + (id -> fileActor)
        val ackMsg = envelopedWithDate(id, Json.toJson(NewSessionAck()))

        shadows.keys.foreach(_ ! ackMsg)

        val toAllMsg = new Envelope(msg.to, Option(id.toString), MessageTypes.DiffSyncType, msg.payload) with ESender with EDated {
          val from: String = msg.from
          val date: DateTime = msg.date
        }

        clients.filterKeys(msg.to).foreach {
          case (_, actors) => actors.foreach(_ ! toAllMsg)
        }
      }

      case CloseSession() => {
        val id = msg.uuid.map(UUID.fromString).getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)

        val ackMsg = new Envelope(msg.to, Option(id.toString), MessageTypes.DiffSyncType, msg.payload) with ESender with EDated {
          val from: String = msg.from
          val date: DateTime = new DateTime()
        }

        fileActor.ask(FileProtocol.Participants).map {
          case FileProtocol.ParticipantsList(actors) => actors.filter(_ != sender()).map(_.actor).foreach (_ ! ackMsg)
          case unknown => log.warning(s"Unknown message in CloseSession: ${unknown.toString}")
        }.recover {
          case ex: Throwable => log.error(ex, "Something went wrong in CloseSession")
        }

        fileActor ! PoisonPill

        val newMsg = envelopedWithDate(id, Json.toJson(CloseSession()))
        sender() ! newMsg
      }

      case AddUser(username) => {
        val id = msg.uuid.map(UUID.fromString).getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)

        val client = clients.find(_._1 == username).fold(log.warning(s"Trying to add client named $username")) {
          case (_, actors) =>
            fileActor.ask(FileProtocol.AddClient(actors.map(actor => Client(username, actor)))).map {
              case FileProtocol.AddClientAck(id, clients, text, participants) => {
                val participantsStr = participants.map(_.username).toSet
                val newSessionMsg = new Envelope(participantsStr, Option(id.toString), MessageTypes.DiffSyncType, Json.toJson(NewSession(text))) with EDated {
                  val date: DateTime = new DateTime()
                }
                clients.map(_.actor).foreach { actor =>
                  actor ! newSessionMsg
                }

                val username = clients.head.username
                val ackMsg = new Envelope(participantsStr + username, Option(id.toString), MessageTypes.DiffSyncType, Json.toJson(AddUser(username))) with EDated {
                  val date: DateTime = new DateTime()
                }

                participants.map(_.actor).foreach { actor =>
                  actor ! ackMsg
                }
              }
            }
        }
      }

      case RemoveUser(username) => {
        val id = msg.uuid.map(UUID.fromString).getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)
        val connections = clients(username)

        connections foreach { actor =>
          fileActor ! FileProtocol.RemoveClientByName(username)
        }

        val ackMsg = new Envelope(Set.empty, Option(id.toString), MessageTypes.DiffSyncType, msg.payload) with EDated with ESender {
          val date: DateTime = new DateTime()
          val from: String = msg.from
        }


        fileActor.ask(FileProtocol.Participants).map {
          case FileProtocol.ParticipantsList(actors) => {
            if(actors.nonEmpty) {
              actors.filter(_ != sender()).map(_.actor) foreach (_ ! ackMsg)
            } else {
              fileActor ! PoisonPill
            }
          }
          case unknown => log.warning(s"Unknown message ${unknown.toString}")
        }.recover {
          case ex: Throwable => log.error(ex, "Something went wrong")
        }
      }

      case innerMsg: Diff with CheckSumMD5 => {
        val id = msg.uuid.map(UUID.fromString).getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)

        fileActor ! FileProtocol.DiffFromClient(sender(), innerMsg.text, innerMsg.md5)
      }

      case unknown => log.warning(s"Unknown message ${unknown.toString}")
    }
  }

  protected def envelopedWithDate(id: UUID, payload: JsValue, dateTime: DateTime = new DateTime()): Envelope with EDated  =
    new Envelope(Set.empty, Option(id.toString), MessageTypes.DiffSyncType, payload) with EDated {
      val date: DateTime = dateTime
    }
}
