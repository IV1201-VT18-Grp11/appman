package controllers

import database.Role
import javax.inject._

import controllers.LoginController.{LoginForm, RegisterForm}
import models.UserManager
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class LoginController @Inject()(implicit cc: ControllerComponents,
                                val userManager: UserManager,
                                executionContext: ExecutionContext)
    extends AbstractController(cc)
    with I18nSupport
    with Security {
  private val loginForm = Form(
    mapping("username" -> nonEmptyText, "password" -> nonEmptyText)(
      LoginForm.apply
    )(LoginForm.unapply)
  )

  private val registerForm = Form(
    mapping("username"        -> nonEmptyText,
            "password"        -> nonEmptyText,
            "confirmPassword" -> nonEmptyText,
            "firstname"       -> nonEmptyText,
            "surname"         -> nonEmptyText,
            "email"           -> nonEmptyText)(RegisterForm.apply)(RegisterForm.unapply)
      .verifying("The passwords you have entered do not match",
                 data => data.password == data.confirmPassword)
  )

  def login(target: Option[String]) = userAction().apply {
    implicit request: Request[AnyContent] =>
      Ok(views.html.login(loginForm, target))
  }

  def register(target: Option[String]) = userAction().apply {
    implicit request: Request[AnyContent] =>
      Ok(views.html.register(registerForm, target))
  }

  def logout() = userAction(Role.User).apply {
    implicit request: Request[AnyContent] =>
      clearUser(Redirect(routes.HomeController.index()), request)
        .flashing("message" -> "You have been logged out")
  }

  private def validateRedirect(target: String) =
    target.startsWith("/") && !target.startsWith("//")

  def doLogin(target: Option[String]) = userAction().async {
    implicit request: Request[AnyContent] =>
      val form = loginForm.bindFromRequest()
      if (form.hasErrors) {
        Future.successful(BadRequest(views.html.login(form, target)))
      } else {
        val creds = form.value.get
        userManager.login(creds.username, creds.password).map {
          case Some(session) =>
            val redirectTarget =
              target
                .filter(validateRedirect)
                .getOrElse(routes.HomeController.index().url)
            setUserSession(Redirect(redirectTarget), request, session)
              .flashing("message" -> "You have been logged in")
          case None =>
            val failedForm =
              form.withError("password", "Invalid username or password")
            BadRequest(views.html.login(failedForm, target))
        }
      }
  }

  def doRegister(target: Option[String]) = userAction().async {
    implicit request: Request[AnyContent] =>
      val form = registerForm.bindFromRequest()
      if (form.hasErrors) {
        Future.successful(BadRequest(views.html.register(form, target)))
      } else {
        val creds = form.value.get
        userManager
          .register(creds.username,
                    creds.password,
                    creds.email,
                    creds.firstname,
                    creds.surname)
          .flatMap {
            case Right(user) =>
              userManager.login(creds.username, creds.password).map { session =>
                val redirectTarget =
                  target
                    .filter(validateRedirect)
                    .getOrElse(routes.HomeController.index().url) // the home page will be the standard alternative
                setUserSession(Redirect(redirectTarget), request, session.get) // if we succeed to register/login we will go the desired page
                  .flashing(
                    "message" -> "You have been registered and logged in"
                  )
              }
            case Left(reasons) =>
              val failedForm = reasons.foldRight(form) {
                case (UserManager.RegistrationError.UsernameTaken, f) =>
                  f.withError("username", "The username is already taken")
                case (UserManager.RegistrationError.EmailTaken, f) =>
                  f.withError("email", "The email is already taken")
              }
              Future.successful(
                BadRequest(views.html.register(failedForm, target))
              ) // if we fail to complete the registration, the page will be reloaded
          }
      }
  }
}

object LoginController {

  case class LoginForm(username: String, password: String)

  case class RegisterForm(username: String,
                          password: String,
                          confirmPassword: String,
                          firstname: String,
                          surname: String,
                          email: String)

}
