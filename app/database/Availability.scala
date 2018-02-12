package database

import java.time.Instant

import database.PgProfile.api._

case class Availability(id: Id[Availability],
                        user: Id[User],
                        applicationId: Id[JobApplication],
                        fromDate: Instant,
                        toDate: Instant) extends HasId {
  type Self = Availability
  type IdType = Long
}

class Availabilities(tag: Tag) extends Table[Availability](tag, "availabilties") {
  def id = column[Id[Availability]]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Id[User]]("user0")
  def applicationId = column[Id[JobApplication]]("application")
  def fromDate = column[Instant]("from")
  def toDate = column[Instant]("from")

  def application = foreignKey("application_fk", applicationId, JobApplications)(_.id)



  override def * = (id, userId, applicationId, fromDate, toDate ) <> (Availability.tupled, Availability.unapply)
}

object Availabilities extends TableQuery[Availabilities](new Availabilities(_))
