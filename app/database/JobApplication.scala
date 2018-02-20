package database

import java.time.Instant

import database.PgProfile.api._

case class JobApplication(id: Id[JobApplication],
                          userId: Id[User],
                          job: Id[Job],
                          description: String,
                          date: Instant = Instant.now())
    extends HasId {
  type Self   = JobApplication
  type IdType = Long
}

class JobApplications(tag: Tag)
    extends Table[JobApplication](tag, "applications") {
  def id          = column[Id[JobApplication]]("id", O.PrimaryKey, O.AutoInc)
  def userId      = column[Id[User]]("user")
  def jobId       = column[Id[Job]]("job")
  def description = column[String]("description")
  def date = column[Instant]("date")

  def user = foreignKey("user_fk", userId, Users)(_.id)
  def job  = foreignKey("job_fk", jobId, Jobs)(_.id)

  override def * =
    (id, userId, jobId, description, date) <> (JobApplication.tupled, JobApplication.unapply)
}

object JobApplications
    extends TableQuery[JobApplications](new JobApplications(_))
