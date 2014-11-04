package controllers

import play.api.mvc.{BodyParsers, Action}

trait AsyncJson {
  def asyncJson[A](action: Action[A]) = Action.async(BodyParsers.parse.json[A]) { request =>
    action(request)
  }
}
