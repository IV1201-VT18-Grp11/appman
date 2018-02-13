package controllers

import java.time.Instant
import java.util.Date
import javax.inject._

import controllers.ApplyController.ApplyForm
import database.{Competence, Id, Job}
import models.{ApplicationManager, JobManager, UserManager}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.mvc._
import sun.security.jca.GetInstance.Instance

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ApplyController @Inject()(implicit cc: ControllerComponents,
                                val userManager: UserManager,
                                executionContext: ExecutionContext,
                                applicationManager: ApplicationManager,
                                jobManager: JobManager)
    extends AbstractController(cc)
    with I18nSupport
    with Security {

  private val applyForm = Form(
    mapping("description"      -> text,
            "expertise"        -> text,
            "experience"       -> text,
            "availabilityFrom" -> date,
            "availabilityTo"   -> date)(ApplyForm.apply)(ApplyForm.unapply)
  )

  private def showApplyForm(
    form: Form[ApplyForm]
  )(implicit req: RequestHeader) =
    for {
      competences <- applicationManager.allCompetences()
    } yield views.html.jobapply(form, competences)

  def jobapply() = userAction().async { implicit request: Request[AnyContent] =>
    showApplyForm(applyForm).map(Ok(_))
  }
  def doApply() = userAction().async { implicit request: Request[AnyContent] =>
    applyForm
      .bindFromRequest()
      .fold(formWithErrors => showApplyForm(formWithErrors).map(BadRequest(_)),
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
        (job, field) <- jobManager.find(jobId).map(_.get)
      } yield Ok(views.html.jobdescription(job))
  }
}

object ApplyController {
  case class ApplyForm(description: String,
                       expertise: String,
                       experience: String,
                       availabilityFrom: Instant,
                       availabilityTo: Instant)
}
