package controllers

import javax.inject._

import controllers.LoginController.LoginForm
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LoginController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) with I18nSupport {
  private val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }

  def doLogin() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm.bindFromRequest()))
  }
}

object LoginController {
  case class LoginForm(username: String, password: String)
}
