package controllers

import javax.inject._

import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LoginController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {
  def login(username: String, password: String) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }
}