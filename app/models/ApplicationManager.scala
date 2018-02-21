package models

import com.google.inject.ImplementedBy
import database.{
  ApplicationCompetence,
  ApplicationCompetences,
  Availabilities,
  Availability,
  Competence,
  Competences,
  Id,
  Job,
  JobApplication,
  JobApplications,
  PgProfile,
  Role,
  User
}
import java.time.LocalDate
import java.time.Period
import scala.concurrent.ExecutionContext
import database.PgProfile.api._
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.Future

@ImplementedBy(classOf[DbApplicationManager])
trait ApplicationManager {
  def allCompetences(): Future[Seq[Competence]]

  def applicationCompetences(
    id: Id[JobApplication]
  ): Future[Seq[(Competence, ApplicationCompetence)]]

  def applicationAvailabilities(
    id: Id[JobApplication]
  ): Future[Seq[Availability]]

  def find(id: Id[JobApplication],
           jobId: Id[Job],
           visitingUser: User): Future[Option[(JobApplication, User, Job)]]

  def create(
    user: Id[User],
    job: Id[Job],
    description: String,
    competences: Map[Id[Competence], Float],
    availabilities: Seq[(LocalDate, LocalDate)]
  ): Future[Id[JobApplication]]

  def all(visitingUser: User): Future[Seq[(JobApplication, Job, User)]]
}

class DbApplicationManager @Inject()(
  implicit protected val dbConfigProvider: DatabaseConfigProvider,
  executionContext: ExecutionContext
) extends ApplicationManager
    with HasDatabaseConfigProvider[PgProfile] {
  private def visibleToUser(user: User) =
    JobApplications
      .filter(_.userId === user.id || user.role >= Role.Employee)

  def allCompetences(): Future[Seq[Competence]] = db.run(Competences.result)

  def applicationCompetences(
    id: Id[JobApplication]
  ): Future[Seq[(Competence, ApplicationCompetence)]] = db.run {
    (for {
      appCompetence <- ApplicationCompetences
      if appCompetence.applicationId === id
      competence <- appCompetence.competence
    } yield (competence, appCompetence)).result
  }

  def applicationAvailabilities(
    id: Id[JobApplication]
  ): Future[Seq[Availability]] = db.run {
    Availabilities.filter(_.applicationId === id).result
  }

  def find(id: Id[JobApplication],
           jobId: Id[Job],
           visitingUser: User): Future[Option[(JobApplication, User, Job)]] =
    db.run {
      (for {
        application <- visibleToUser(visitingUser)
        if application.id === id && application.jobId === jobId
        user <- application.user
        job  <- application.job
      } yield (application, user, job)).result.headOption
    }

  def create(
    user: Id[User],
    job: Id[Job],
    description: String,
    competences: Map[Id[Competence], Float],
    availabilities: Seq[(LocalDate, LocalDate)]
  ): Future[Id[JobApplication]] = db.run {
    (for {
      id <- JobApplications
        .returning(JobApplications.map(_.id)) += JobApplication(
        Id[JobApplication](-1),
        user,
        job,
        description
      )
      _ <- ApplicationCompetences ++= competences.map {
        case ((competence, years)) =>
          ApplicationCompetence(competence, years, id)
      }
      _ <- Availabilities ++= availabilities.map {
        case (from, to) =>
          Availability(Id[Availability](-1), id, from, to)
      }
    } yield id).transactionally
  }

  override def all(
    visitingUser: User
  ): Future[Seq[(JobApplication, Job, User)]] =
    db.run {
      (for {
        jobApplication <- visibleToUser(visitingUser)
        job            <- jobApplication.job
        user           <- jobApplication.user
      } yield (jobApplication, job, user)).result
    }
}
