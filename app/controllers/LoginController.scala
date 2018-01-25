package controllers

import javax.inject._

import controllers.LoginController.LoginForm
import models.UserManager
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LoginController @Inject()(cc: ControllerComponents,
                                protected val userManager: UserManager)
  extends AbstractController(cc) with I18nSupport with Security {
  private val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }

  def doLogin() = Action.async { implicit request: Request[AnyContent] =>
    val form = loginForm.bindFromRequest()
    if (form.hasErrors) {
      Future.successful(BadRequest(views.html.login(form)))

    } else {
      val creds = form.value.get
      userManager.login(creds.username, creds.password).map {
        case Some(user) =>
          setUser(Redirect(routes.HomeController.index()), request, user)
        case None =>
          val failedForm = form.withError("password", "Invalid username or password")
          BadRequest(views.html.login(failedForm))
      }
    }
  }
}

object LoginController {
  case class LoginForm(username: String, password: String)
}
