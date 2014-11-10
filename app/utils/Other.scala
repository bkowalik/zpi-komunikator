package utils

import exceptions.MissingConfigurationEntry

trait Other extends OtherKeys{
  import play.api.Play.current

  lazy val appSalt = current.configuration.getString(appSaltKey).getOrElse(throw MissingConfigurationEntry(appSaltKey))
}
