package controllers

import javax.inject._

import controllers.ApplyController.ApplyForm
import database.{Competence, Id}
import models.{ApplicationManager, UserManager}
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
                                executionContext: ExecutionContext,
                                applicationManager: ApplicationManager)
    extends AbstractController(cc)
    with I18nSupport
    with Security {

  private val applyForm = Form(
    mapping("username"        -> nonEmptyText,
            "password"        -> nonEmptyText,
            "confirmPassword" -> nonEmptyText,
            "firstname"       -> nonEmptyText,
            "surname"         -> nonEmptyText,
            "email"           -> nonEmptyText,
    )(ApplyForm.apply)(ApplyForm.unapply)
  )

  private def showApplyForm(
    form: Form[ApplyForm]
  )(implicit req: RequestHeader) =
    for {
      competences <- applicationManager.allCompetences()
    } yield views.html.jobapply(form, competences)

  def jobapply() = userAction.async { implicit request: Request[AnyContent] =>
    showApplyForm(applyForm).map(Ok(_))
  }
  def doApply() = userAction.async { implicit request: Request[AnyContent] =>
    applyForm
      .bindFromRequest()
      .fold(formWithErrors => showApplyForm(formWithErrors).map(BadRequest(_)),
            application => ???)
  }
}

object ApplyController {
  case class ApplyForm(username: String,
                       password: String,
                       confirmPassword: String,
                       firstname: String,
                       surname: String,
                       email: String)
}
