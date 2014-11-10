package exceptions

case class MissingConfigurationEntry(msg: String) extends Exception(msg)