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
    users.insert(User(Option(UUID.randomUUID()), username, password, email)).run == 1
  }

  def allUsers = database.withTransaction { implicit session =>
    users.map(_.username).list
  }
}
