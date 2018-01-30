package controllers

import javax.inject._

import controllers.LoginController.{LoginForm, RegisterForm}
import models.UserManager
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._
import scala.concurrent.{ ExecutionContext, Future }

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LoginController @Inject()(implicit cc: ControllerComponents,
                                val userManager: UserManager,
                                executionContext: ExecutionContext)
  extends AbstractController(cc) with I18nSupport with Security {
  private val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(LoginForm.apply)(LoginForm.unapply)
  )

  private val registerForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText,
      "firstname" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "email" -> nonEmptyText,
    )(RegisterForm.apply)(RegisterForm.unapply)
  )

  def login() = userAction.apply { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }

  def register() = userAction.apply { implicit request: Request[AnyContent] =>
    Ok(views.html.register(registerForm))
  }

  def logout() = userAction.apply { implicit request: Request[AnyContent] =>
    clearUser(Redirect(routes.HomeController.index()), request)
      .flashing("message" -> "You have been logged out")
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
            .flashing("message" -> "You have been logged in")
        case None =>
          val failedForm = form.withError("password", "Invalid username or password")
          BadRequest(views.html.login(failedForm))
      }
    }
  }

  def doRegister() = Action.async { implicit request: Request[AnyContent] =>
    val form = registerForm.bindFromRequest()
    if (form.hasErrors) {
      Future.successful(BadRequest(views.html.register(form)))

    } else {
      val creds = form.value.get
      userManager.register(creds.username, creds.password).map {
        case Some(user) =>
          setUser(Redirect(routes.HomeController.index()), request, user)
        case None =>
          val failedForm = form.withError("username", "The username is already in use")
          BadRequest(views.html.register(failedForm))
      }
    }
  }
}

object LoginController {
  case class LoginForm(username: String, password: String)
  case class RegisterForm(username: String, password: String, confirmPassword: String,
                          firstname: String, surname: String, email: String)
}
