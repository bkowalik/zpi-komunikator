package repositories

import java.util.UUID

import models.{User, Users}

import scala.slick.driver.H2Driver.simple._

class UsersRepository(database: Database) {
  assert(database != null)

  val users = TableQuery[Users]

  def findByUsername(username: String) = database.withTransaction { implicit session =>
    users.filter(_.username === username).firstOption
  }

  def createUser(username: String, password: String, email: String) = database.withTransaction { implicit session=>
    if(!users.filter(_.username === username).exists.run) {
      users.insert(User(Option(UUID.randomUUID()), username, password, email)).run == 1
    } else {
      false
    }
  }

  def allUsers = database.withTransaction { implicit session =>
    users.map(_.username).list
  }

  def setPassword(username: String, password: String) = database.withTransaction { implicit session =>
    users.filter(_.username === username).map(_.password).update(password)
  }
}
