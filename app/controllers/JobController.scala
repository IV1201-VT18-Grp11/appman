package controllers

import database.JobApplication
import java.time.LocalDate
import javax.inject._

import controllers.JobController.{ApplyForm, AvailabilityField, CompetenceField}
import controllers.MoreForms._
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
    mapping("description" -> nonEmptyText,
            "competences" -> seq(
              mapping(
                "id"              -> id[Competence](longNumber),
                "experienceYears" -> bigDecimal
              )(CompetenceField.apply)(CompetenceField.unapply)
            ),
            "availabilities" -> seq(
              mapping("from" -> localDate, "to" -> localDate)(
                AvailabilityField.apply
              )(AvailabilityField.unapply)
            ))(ApplyForm.apply)(ApplyForm.unapply)
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
        .fold(
          formWithErrors =>
            showApplyForm(formWithErrors, jobId).map(BadRequest(_)),
          application =>
            applicationManager
              .create(request.user.get.id,
                      jobId,
                      application.description,
                      application.competences
                        .map(
                          competence =>
                            (competence.id, competence.experienceYears.toFloat)
                        )
                        .toMap,
                      application.availabilities.map(
                        availability => (availability.from, availability.to)
                      ))
              .map(
                appId =>
                  Redirect(
                    routes.JobController.applicationDescription(jobId, appId)
                )
            )
        )
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

  def applicationDescription(jobId: Id[Job], appId: Id[JobApplication]) = TODO

  def applicationList() = userAction().async {
    implicit request: Request[AnyContent] =>
      for {
        listings <- applicationManager.all(request.user.get)
      } yield Ok(views.html.applicationlist(listings))
  }
}

object JobController {
  case class ApplyForm(description: String,
                       competences: Seq[CompetenceField],
                       availabilities: Seq[AvailabilityField])
  case class CompetenceField(id: Id[Competence], experienceYears: BigDecimal)
  case class AvailabilityField(from: LocalDate, to: LocalDate)
}
