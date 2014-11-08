package controllers

import play.api.mvc._
import Security.Authenticated

import scala.concurrent.Future

trait Secured {
  this: Controller =>

  def username(request: RequestHeader) = request.session.get("username")

  def onUnauthorized(request: RequestHeader) = Results.Unauthorized("")

  def withAuth(f: String => Request[AnyContent] => Result) = {
    Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  def withAsyncAuth(f: String => Request[AnyContent] => Future[Result]) = {
    Authenticated(username, onUnauthorized) { user =>
      Action.async(request => f(user)(request))
    }
  }
}
