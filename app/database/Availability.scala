package database

import java.time.LocalDate

import database.PgProfile.api._

/**
  * A single continuous stretch of time where the applicant is available.
  */
case class Availability(id: Id[Availability],
                        applicationId: Id[JobApplication],
                        from: LocalDate,
                        to: LocalDate)
    extends HasId {
  type Self   = Availability
  type IdType = Long
}

class Availabilities(tag: Tag)
    extends Table[Availability](tag, "availabilities") {
  def id            = column[Id[Availability]]("id", O.PrimaryKey, O.AutoInc)
  def applicationId = column[Id[JobApplication]]("application")
  def from          = column[LocalDate]("from")
  def to            = column[LocalDate]("to")

  def application =
    foreignKey("application_fk", applicationId, JobApplications)(_.id)

  override def * =
    (id, applicationId, from, to) <> (Availability.tupled, Availability.unapply)
}

object Availabilities extends TableQuery[Availabilities](new Availabilities(_))
