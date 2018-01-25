package database

import java.sql.Timestamp
import PgProfile.api._


case class Job (id: Id[Job],
                field_id: Id[Field],
                name: String,
                from_date: Timestamp,
                to_date: Option[Timestamp],
                country: String) extends HasId {
  type Self = Job
  type IdType = Long
}


class Jobs(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[Id[Job]]("job_id", O.PrimaryKey, O.AutoInc)
  def fieldId = column[Id[Field]] ("field_id")
  def name = column[String] ("name")
  def from_date = column[Timestamp] ("from_date")
  def to_date = column[Option[Timestamp]] ("to_date")
  def country = column[String] ("country")

  def field = foreignKey("field_fk", fieldId, Fields)(_.id)

  /**
    *
    */
  override def * = (id, fieldId, name, from_date, to_date, country) <> (Job.tupled, Job.unapply)
}

/**
  *
  */
object Jobs extends TableQuery[Jobs](new Jobs(_))
