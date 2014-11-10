package actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpecLike}
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, JsValue, Json}
import protocol._
import services.{ManagerService, DatabaseService}
import org.mockito.Mockito._
import scala.concurrent.duration._

class DummyActor extends Actor with ActorLogging {
  def receive = {
    case foo => log.info(foo.toString)
  }
}

class IntegrationActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar{
  def this() = this(ActorSystem("test-system"))

  val dummyInstance = system.actorOf(Props[DummyActor])

  it should "all work together" in {
    val dbMock = mock[DatabaseService]
    when(dbMock.database) thenReturn(dummyInstance)
    val manager = system.actorOf(ClientsManager.props(dbMock))
    val managerServiceMock = mock[ManagerService]
    when(managerServiceMock.getWorker) thenReturn(manager)

    val maniek = system.actorOf(ClientTalkActor.props("zenek", managerServiceMock, dummyInstance))
    val zenek = system.actorOf(ClientTalkActor.props("maniek", managerServiceMock, self))

    val msg = TextMessage("hiho!")
    val jsonMsg = Json.obj(
      "message" -> "hiho!"
    )
    val jsonEnv = Json.obj(
      "to" -> Set("maniek"),
      "kind" -> MessageTypes.TextMessageType.toString,
      "payload" -> jsonMsg
    )

    val loggedIn = receiveOne(5 seconds).asInstanceOf[JsObject]

    (loggedIn \ "kind").as[String] shouldBe MessageTypes.UserLoggedInType.toString


    maniek ! jsonEnv
    maniek ! PoisonPill

    val received = receiveOne(5 seconds).asInstanceOf[JsObject]

    (received \ "to").as[Set[String]] shouldBe Set("maniek")
    (received \ "kind").as[String] shouldBe MessageTypes.TextMessageType.toString
    (received \ "from").as[String] shouldBe "zenek"

    val loggedOut = receiveOne(5 seconds).asInstanceOf[JsObject]
    (loggedOut \ "kind").as[String] shouldBe MessageTypes.UserLoggedOutType.toString
  }

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
}
