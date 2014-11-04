import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ZPIServer extends Build {

  lazy val akka = Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % "2.3.6",
    "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.6" % "test"
  )

  lazy val slick = "com.typesafe.slick" % "slick_2.11" % "2.1.0"

  lazy val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test"

  lazy val deps = Seq(jdbc, anorm, cache, ws, slick, scalaTest) ++ akka

  lazy val main = Project("zpi-server", file(".")).enablePlugins(play.PlayScala)
    .settings(
      version := "0.1",
      scalaVersion := "2.11.3",
      libraryDependencies ++= deps
    )
}
