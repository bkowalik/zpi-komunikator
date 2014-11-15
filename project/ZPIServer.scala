import sbt._
import Keys._
import play.Play.autoImport._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._


object ZPIServer extends Build {

  lazy val akka = Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % "2.3.6",
    "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.6" % "test"
  )

  lazy val slick = "com.typesafe.slick" % "slick_2.11" % "2.1.0"

  lazy val jodaTime = "joda-time" % "joda-time" % "2.5"

  lazy val swagger = "com.wordnik" %% "swagger-play2" % "1.3.10"

  lazy val macWireRuntime = "com.softwaremill.macwire" % "runtime_2.11" % "0.7.3"

  lazy val macWire = "com.softwaremill.macwire" % "macros_2.11" % "0.7.3"

  lazy val scalaBcrypt = "com.github.t3hnar" % "scala-bcrypt_2.11" % "2.4"

  lazy val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test"

  lazy val mockito = "org.mockito" % "mockito-all" % "1.9.0" % "test"

  lazy val diffMatchPatch = "com.sksamuel.diff" % "diff" % "1.1.11"

  lazy val deps = Seq(jdbc, anorm, slick, jodaTime, macWireRuntime, macWire, scalaBcrypt, swagger, scalaTest,
                       mockito, diffMatchPatch) ++ akka

  lazy val main = Project("zpi-server", file(".")).enablePlugins(play.PlayScala)
    .settings(
      version := "0.1",
      scalaVersion := "2.11.3",
      libraryDependencies ++= deps,
      maintainer in Linux := "mgw",
      packageSummary in Linux := "Developers' communicator",
      packageDescription := "stub",
      debianPackageDependencies in Debian ++= Seq("java-runtime")
    )

  //System.setProperty("macwire.debug", "")
}
