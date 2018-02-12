package models

import com.google.inject.ImplementedBy
import database.{
  ApplicationCompetence,
  ApplicationCompetences,
  Competence,
  Competences,
  Id,
  Job,
  JobApplication,
  JobApplications,
  PgProfile,
  User
}
import scala.concurrent.ExecutionContext
import database.PgProfile.api._
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.Future

@ImplementedBy(classOf[DbApplicationManager])
trait ApplicationManager {
  def allCompetences(): Future[Seq[Competence]]

  def create(
    user: Id[User],
    job: Id[Job],
    description: String,
    competences: Map[Id[Competence], Float]
  )(implicit ec: ExecutionContext): Future[Id[JobApplication]]
}

class DbApplicationManager @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
) extends ApplicationManager
    with HasDatabaseConfigProvider[PgProfile] {
  def allCompetences(): Future[Seq[Competence]] = db.run(Competences.result)

  def create(
    user: Id[User],
    job: Id[Job],
    description: String,
    competences: Map[Id[Competence], Float]
  )(implicit ec: ExecutionContext): Future[Id[JobApplication]] = db.run {
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
    } yield id).transactionally
  }
}
