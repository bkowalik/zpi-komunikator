package actors

import java.security.MessageDigest
import java.util.UUID

import actors.FileProtocol._
import akka.actor.{Props, Actor, ActorLogging, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.sksamuel.diffpatch.DiffMatchPatch
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpecLike}
import scala.concurrent.duration._

//
//class DummyActor extends Actor with ActorLogging {
//  def receive = {
//    case foo => log.info(foo.toString)
//  }
//}

class FileOperationsTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {
  def this() = this(ActorSystem("file-test"))

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString
  }

  lazy val dmp = new DiffMatchPatch

  it should "create diff and patch" in {
    val old = "Macs had the original point and click UI."
    val `new` = "Macintoshes had the original point and click interface."

    val patch = dmp.patch_make(old, `new`)
    val ret = dmp.patch_apply(patch, old)

    val patchedText = ret(0).asInstanceOf[String]

    patchedText shouldEqual `new`
  }

  val oldSource =
    """
      |case class Foo(bar: String, baz: Map[Int, ActorRef]) extends FooTrait
      |case class Wibble(wobble: Int) extends WibbleTrait
    """.stripMargin

  val newSource =
    """
      |case class Foo(bar: String, baz: Map[Int, Wibble]) extends FooTrait
      |class Wibble(wobble: Double) extends WibbleTrait
    """.stripMargin

  it should "work with FileActor" in {
    val fileActor = system.actorOf(FileActor.props(UUID.randomUUID(), oldSource, Map.empty), "fileActor")

    val maniek = system.actorOf(Props[DummyActor], "maniek")
    val zenek = system.actorOf(Props[DummyActor], "zenek")

    fileActor ! AddClient(Set(Client("maniek", maniek)))
    receiveOne(5 seconds) shouldBe a[AddClientAck]
    fileActor ! AddClient(Set(Client("zenek", zenek)))
    receiveOne(5 seconds) shouldBe a[AddClientAck]

    val patchText = dmp.patch_toText(dmp.patch_make(oldSource, newSource))

    fileActor ! DiffFromClient(Client("maniek", maniek), patchText, md5(newSource))

    fileActor ! GetText

    val response = receiveOne(5 seconds).asInstanceOf[TestText].text

    response shouldEqual newSource
  }
}
