package database

import java.sql.Timestamp

import database.PgProfile.api._

/**
  * A single job listing.
  */
case class Job(id: Id[Job],
               fieldId: Id[Field],
               name: String,
               fromDate: Timestamp,
               toDate: Option[Timestamp],
               country: String,
               description: String,
               requirement: String)
    extends HasId {
  type Self   = Job
  type IdType = Long
}

class Jobs(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[Id[Job]]("id", O.PrimaryKey, O.AutoInc)

  def fieldId = column[Id[Field]]("field")

  def name = column[String]("name")

  def fromDate = column[Timestamp]("from_date")

  def toDate = column[Option[Timestamp]]("to_date")

  def country = column[String]("country")

  def description = column[String]("description")

  def requirement = column[String]("requirement")

  def field = foreignKey("field_fk", fieldId, Fields)(_.id)

  /**
    *
    */
  override def * =
    (id, fieldId, name, fromDate, toDate, country, description, requirement) <> (Job.tupled, Job.unapply)
}

/**
  *
  */
object Jobs extends TableQuery[Jobs](new Jobs(_))
