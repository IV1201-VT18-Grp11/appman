package controllers

import javax.inject._

import controllers.ApplyController.{ApplyForm}
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
class ApplyController @Inject()(implicit cc: ControllerComponents,
                                val userManager: UserManager,
                                executionContext: ExecutionContext)
  extends AbstractController(cc) with I18nSupport with Security {


  private val applyForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText,
      "firstname" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "email" -> nonEmptyText,
    )(ApplyForm.apply)(ApplyForm.unapply)
  )
  def jobapply() = userAction.apply { implicit request: Request[AnyContent] =>
    Ok(views.html.jobapply(applyForm))
  }
  def doApply() = userAction.apply { implicit request: Request[AnyContent] =>
    val form = applyForm.bindFromRequest()
    BadRequest(views.html.jobapply(form))
  }
}


object ApplyController {
  case class ApplyForm(username: String, password: String, confirmPassword: String,
                          firstname: String, surname: String, email: String)
}
