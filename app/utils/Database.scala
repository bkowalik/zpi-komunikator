package utils

import exceptions.MissingConfigurationEntry

import scala.slick.driver.H2Driver.simple._

trait Database extends DatabaseKeys {
  import play.api.Play.current

  lazy val dbDriver = current.configuration.getString(dbDriverKey).getOrElse(throw MissingConfigurationEntry(dbDriverKey))

  lazy val dbURL = current.configuration.getString(dbURLKey).getOrElse(throw MissingConfigurationEntry(dbURLKey))

  lazy val dbUser = current.configuration.getString(dbUserKey).getOrElse(throw MissingConfigurationEntry(dbUserKey))

  lazy val dbPassword = current.configuration.getString(dbPasswordKey).getOrElse(throw MissingConfigurationEntry(dbPasswordKey))

  lazy val database = Database.forURL(dbURL, dbUser, dbPassword, driver=dbDriver)
}
