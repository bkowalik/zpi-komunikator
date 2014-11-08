package controllers

import actors.ManagerProtocol.FriendsList
import com.wordnik.swagger.annotations._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import protocol.{FailureMessage, RestProtocol, SuccessMessage}
import services.ManagerService
import akka.pattern.ask

import scala.concurrent.Future

@Api(value = "/users", description = "Users operations")
class UsersController(managerService: ManagerService) extends Controller with ControllerUtils {

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

  def checkOnline = Action.async { implicit request =>
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

}
