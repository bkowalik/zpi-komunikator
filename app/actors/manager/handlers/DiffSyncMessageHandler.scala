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
        val id = msg.uuid.getOrElse(throw new Exception("Missing document share id"))

        val from = clients(msg.from).map(actor => Client(msg.from, actor))
        val to = clients.filterKeys(msg.to).flatMap {
          case (username, actors) => actors.map(actor => Client(username, actor))
        }

        val shadows = (from ++ to).map(client => client -> text).toMap

        val fileActor = context.system.actorOf(FileActor.props(id, text, shadows))

        diffSyncs = diffSyncs + (id -> fileActor)

        val toAllMsg = new Envelope(msg.to, msg.uuid, MessageTypes.DiffSyncType, msg.payload) with ESender with EDated {
          val from: String = msg.from
          val date: DateTime = msg.date
        }

        shadows.keys.filter(_.actor != sender()).foreach(_.actor ! toAllMsg)
      }

      case CloseSession() => {
        val id = msg.uuid.getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)

        val ackMsg = new Envelope(msg.to, Option(id), MessageTypes.DiffSyncType, msg.payload) with ESender with EDated {
          val from: String = msg.from
          val date: DateTime = new DateTime()
        }

        fileActor.ask(FileProtocol.Participants).map {
          case FileProtocol.ParticipantsList(actors) => actors.filter(_.actor != sender()).map(_.actor).foreach (_ ! ackMsg)
          case unknown => log.warning(s"Unknown message in CloseSession: ${unknown.toString}")
        }.recover {
          case ex: Throwable => log.error(ex, "Something went wrong in CloseSession")
        }

        fileActor ! PoisonPill
      }

      case AddUser(username) => {
        val id = msg.uuid.getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)

        val client = clients.find(_._1 == username).fold(log.warning(s"Trying to add client named $username")) {
          case (_, actors) =>
            fileActor.ask(FileProtocol.AddClient(actors.map(actor => Client(username, actor)))).map {
              case FileProtocol.AddClientAck(id, clients, text, participants) => {
                val participantsStr = participants.map(_.username).toSet
                val newSessionMsg = new Envelope(participantsStr, Option(id), MessageTypes.DiffSyncType, Json.toJson(NewSession(text))) with EDated {
                  val date: DateTime = new DateTime()
                }
                clients.map(_.actor).foreach { actor =>
                  actor ! newSessionMsg
                }

                val username = clients.head.username
                val ackMsg = new Envelope(participantsStr + username, Option(id), MessageTypes.DiffSyncType, Json.toJson(AddUser(username))) with EDated {
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
        val id = msg.uuid.getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)
        val connections = clients(username)

        connections foreach { actor =>
          fileActor ! FileProtocol.RemoveClientByName(username)
        }

        val ackMsg = new Envelope(Set.empty, Option(id), MessageTypes.DiffSyncType, msg.payload) with EDated with ESender {
          val date: DateTime = new DateTime()
          val from: String = msg.from
        }


        fileActor.ask(FileProtocol.Participants).map {
          case FileProtocol.ParticipantsList(actors) => {
            if(actors.nonEmpty) {
              actors.filter(_.actor != sender()).map(_.actor) foreach (_ ! ackMsg)
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
        val id = msg.uuid.getOrElse(throw new Exception("Missing field id in DiffSyncType message"))
        val fileActor = diffSyncs(id)

        fileActor ! FileProtocol.DiffFromClient(Client(msg.from, sender()), innerMsg.text, innerMsg.md5)
      }

      case unknown => log.warning(s"Unknown message ${unknown.toString}")
    }
  }

  protected def envelopedWithDate(id: UUID, payload: JsValue, dateTime: DateTime = new DateTime()): Envelope with EDated  =
    new Envelope(Set.empty, Option(id), MessageTypes.DiffSyncType, payload) with EDated {
      val date: DateTime = dateTime
    }
}
