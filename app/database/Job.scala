package database

import java.sql.Timestamp
import PgProfile.api._


case class Job (id: Long,
                field_id: Long,
                name: String,
                from_date: Timestamp,
                to_date: Option[Timestamp])


class Jobs(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[Long]("job_id", O.PrimaryKey, O.AutoInc)
  def field_id = column[Long] ("field_id")
  def name = column[String] ("name")
  def from_date = column[Timestamp] ("from_date")
  def to_date = column[Option[Timestamp]] ("to_date")

  /**
    *
    */
  override def * = (id, field_id, name, from_date, to_date) <> (Job.tupled, Job.unapply)
}

/**
  *
  */
object Jobs extends TableQuery[Jobs](new Jobs(_))