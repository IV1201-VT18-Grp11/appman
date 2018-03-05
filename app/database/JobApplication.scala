package database

import java.time.Instant

import database.PgProfile.api._

/**
  * An application made to a [[Job]] listing made by a [[User]].
  */
case class JobApplication(id: Id[JobApplication],
                          userId: Id[User],
                          job: Option[Id[Job]],
                          description: Option[String],
                          date: Option[Instant] = Some(Instant.now()),
                          accepted: Option[Boolean] = None)
    extends HasId {
  type Self   = JobApplication
  type IdType = Long
}

class JobApplications(tag: Tag)
    extends Table[JobApplication](tag, "applications") {
  def id = column[Id[JobApplication]]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Id[User]]("user")

  def jobId = column[Option[Id[Job]]]("job")

  def description = column[Option[String]]("description")

  def date = column[Option[Instant]]("date")

  def accepted = column[Option[Boolean]]("accepted")

  def user = foreignKey("user_fk", userId, Users)(_.id)

  def job = foreignKey("job_fk", jobId, Jobs)(_.id.?)

  override def * =
    (id, userId, jobId, description, date, accepted) <> (JobApplication.tupled, JobApplication.unapply)
}

object JobApplications
    extends TableQuery[JobApplications](new JobApplications(_))
