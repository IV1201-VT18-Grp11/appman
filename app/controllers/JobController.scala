package controllers

import database.{JobApplication, Role}
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

import scala.concurrent.ExecutionContext

/**
  * Contains [[Action]]s related to job listings and applications.
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
                "experienceYears" -> optional(bigDecimal)
              )(CompetenceField.apply)(CompetenceField.unapply)
            ),
            "availabilities" -> seq(
              mapping("from" -> localDate, "to" -> localDate)(
                AvailabilityField.apply
              )(AvailabilityField.unapply)
                .verifying(
                  "The availability period must not end before it begins",
                  availability => !availability.to.isBefore(availability.from)
                )
            ))(ApplyForm.apply)(ApplyForm.unapply)
  )

  private def applyFormWithCompetences(competences: Seq[Competence]) =
    applyForm.copy(data = competences.zipWithIndex.map {
      case ((competence, i)) =>
        s"competences[${i}].id" -> competence.id.raw.toString
    }.toMap)

  private def showApplyForm(form: Form[ApplyForm],
                            jobId: Id[Job])(implicit req: RequestHeader) =
    for {
      competences <- applicationManager.allCompetences()
      competencesMap = competences
        .map(competence => competence.id.raw.toString -> competence)
        .toMap
      (job, _) <- jobManager.find(jobId).getOr404
    } yield views.html.jobapply(form, job, competencesMap)

  def applyForJob(jobId: Id[Job]) = userAction(Role.Applicant).async {
    implicit request: Request[AnyContent] =>
      for {
        competences <- applicationManager.allCompetences()
        form = applyFormWithCompetences(competences)
        view <- showApplyForm(form, jobId)
      } yield Ok(view)
  }

  def doApplyForJob(jobId: Id[Job]) = userAction(Role.Applicant).async {
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
                        .flatMap(
                          competence =>
                            competence.experienceYears
                              .map(_.toFloat)
                              .filter(_ > 0)
                              .map(years => (competence.id, years))
                        )
                        .toMap,
                      application.availabilities.map(
                        availability => (availability.from, availability.to)
                      ))
              .map(
                appId =>
                  Redirect(routes.JobController.applicationDescription(appId))
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

  def applicationDescription(appId: Id[JobApplication]) =
    userAction(Role.Applicant).async { implicit request: Request[AnyContent] =>
      for {
        (application, job, user) <- applicationManager
          .find(appId, request.user.get)
          .getOr404
        competences    <- applicationManager.applicationCompetences(appId)
        availabilities <- applicationManager.applicationAvailabilities(appId)
      } yield
        Ok(
          views.html.applicationdescription(application,
                                            user,
                                            job,
                                            competences,
                                            availabilities)
        )

    }

  def applicationList() = userAction(Role.Applicant).async {
    implicit request: Request[AnyContent] =>
      for {
        applications <- applicationManager.all(request.user.get)
      } yield Ok(views.html.applicationlist(applications))
  }

  def setApplicationStatus(appId: Id[JobApplication]) =
    userAction(Role.Employee).async { implicit request: Request[AnyContent] =>
      val accepted = request.body.asFormUrlEncoded.get("status").head match {
        case "accept" => true
        case "deny"   => false
      }
      for {
        () <- applicationManager.setStatus(appId, accepted)
      } yield
        Redirect(routes.JobController.applicationDescription(appId))
          .flashing("message" -> "The status has been updated")
    }
}

object JobController {
  case class ApplyForm(description: String,
                       competences: Seq[CompetenceField],
                       availabilities: Seq[AvailabilityField])
  case class CompetenceField(id: Id[Competence],
                             experienceYears: Option[BigDecimal])
  case class AvailabilityField(from: LocalDate, to: LocalDate)
}
