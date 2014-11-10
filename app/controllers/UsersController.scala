package controllers

import actors.ManagerProtocol.FriendsList
import actors.repo.UsersProtocol.{LoginFailure, LoginSuccessful}
import com.wordnik.swagger.annotations._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Security, Session, Action, Controller}
import protocol.{FailureMessage, RestProtocol, SuccessMessage}
import services.{UsersService, ManagerService}
import akka.pattern.ask

import scala.concurrent.Future

@Api(value = "/users", description = "Users operations")
class UsersController(managerService: ManagerService, usersService: UsersService) extends Controller with ControllerUtils with Secured {

  case class RegisterForm(username: String, password: String, email: String)

  val registerForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText(6),
      "email" -> email
    )(RegisterForm.apply)(RegisterForm.unapply)
  )

  @ApiOperation(nickname = "", value = "", notes = "", response = classOf[RestProtocol], httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "User created"),
    new ApiResponse(code = 400, message = "Form erros"),
    new ApiResponse(code = 500, message = "50x family errors, server problem (database etc)")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "Register form", required = true, dataType = "RegisterForm", paramType = "body")
  ))
  def register = Action.async { implicit request =>
    Future { registerForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(Json.toJson(FailureMessage(formWithErrors.errors)))
      },
      userData => {
        //register user
        Ok(Json.toJson(SuccessMessage("Account created")))
      }
    )}
  }

  case class CheckFriends(friends: List[String])

  val checkFriendsForm = Form(
    mapping(
      "friends" -> list(text)
    )(CheckFriends.apply)(CheckFriends.unapply)
  )

  def checkOnline = withAsyncAuth { username => implicit request =>
    checkFriendsForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(FailureMessage(formWithErrors.errors))))
      },
      userData => {
        managerService.checkFriends(userData.friends).map {
          case FriendsList(friends) => Ok(Json.toJson(friends))
        }.recover{ case ex => InternalServerError(ex.toString) }
      }
    )
  }

  def checkAllOnline = withAsyncAuth { username => implicit request =>
    managerService.checkAllOnline().map {
      case list: Iterable[String] => Ok(Json.obj("online" -> Json.toJson(list)))
      case _ => InternalServerError("Bad response")
    }.recover{ case ex => InternalServerError(ex.toString) }
  }

  case class LoginForm(username: String, password: String)

  val loginFormForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText(6)
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def doLogin = Action.async { implicit request =>
    loginFormForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(FailureMessage(formWithErrors.errors)))),
      loginData => {
        usersService.login(loginData.username, loginData.password).map {
          case LoginSuccessful => {
            val sessionInfo = Session(Map(Security.username -> loginData.username))
            Ok(Json.toJson(SuccessMessage(""))).withSession(sessionInfo)
          }
          case LoginFailure => Forbidden(Json.obj("general" -> List("Wrong username or password")))
        }.recover{ case ex => InternalServerError(ex.toString) }
      }
    )
  }

  def logout = withAuth { username => request =>
    Ok(Json.toJson(SuccessMessage(""))).withNewSession
  }
}
