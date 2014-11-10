package models

import java.util.UUID

import scala.slick.driver.H2Driver.simple._

case class User(id: Option[UUID], username: String, password: String, email: String)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def username = column[String]("username")
  def password = column[String]("password")
  def email = column[String]("email")

  def * = (id.?, username, password, email) <> (User.tupled, User.unapply)
}