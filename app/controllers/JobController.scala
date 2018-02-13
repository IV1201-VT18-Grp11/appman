package controllers

import javax.inject._

import controllers.ApplyController.ApplyForm
import database.{Competence, Id, Job}
import models.{ApplicationManager, JobManager, UserManager}
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
class JobController @Inject()(implicit cc: ControllerComponents,
                              val userManager: UserManager,
                              executionContext: ExecutionContext,
                              applicationManager: ApplicationManager,
                              jobManager: JobManager)
    extends AbstractController(cc)
    with I18nSupport
    with Security
    with NotFoundHelpers {

  private val applyForm = Form(
    mapping("username"        -> nonEmptyText,
            "password"        -> nonEmptyText,
            "confirmPassword" -> nonEmptyText,
            "firstname"       -> nonEmptyText,
            "surname"         -> nonEmptyText,
            "email"           -> nonEmptyText,
    )(ApplyForm.apply)(ApplyForm.unapply)
  )

  private def showApplyForm(form: Form[ApplyForm],
                            jobId: Id[Job])(implicit req: RequestHeader) =
    for {
      competences <- applicationManager.allCompetences()
      (job, _)    <- jobManager.find(jobId).getOr404
    } yield views.html.jobapply(form, job, competences)

  def applyForJob(jobId: Id[Job]) = userAction().async {
    implicit request: Request[AnyContent] =>
      showApplyForm(applyForm, jobId).map(Ok(_))
  }
  def doApplyForJob(jobId: Id[Job]) = userAction().async {
    implicit request: Request[AnyContent] =>
      applyForm
        .bindFromRequest()
        .fold(formWithErrors =>
                showApplyForm(formWithErrors, jobId).map(BadRequest(_)),
              application => ???)
  }

  def jobList() = userAction().async { implicit request: Request[AnyContent] =>
    for {
      listings <- jobManager.jobListings()
    } yield Ok(views.html.joblist(listings))
  }

  def jobDescription(jobId: Id[Job]) = userAction().async {
    implicit request: Request[AnyContent] =>
      for {
        (job, field) <- jobManager.find(jobId).getOr404
      } yield Ok(views.html.jobdescription(job))
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
