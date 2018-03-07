package models

import javax.inject.Inject

import com.google.inject.ImplementedBy
import database.PgProfile.api._
import database._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.Future

/**
  * Stores job listings.
  */
@ImplementedBy(classOf[DbJobManager])
trait JobManager {

  /**
    * We want to show 0 or more jobs, as a sequence
    */
  def jobListings(): Future[Seq[(Job, Field)]]

  def find(id: Id[Job]): Future[Option[(Job, Field)]]
}

/**
  * Stores job listings in the database.
  */
class DbJobManager @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
) extends JobManager
    with HasDatabaseConfigProvider[PgProfile] {

  override def jobListings(): Future[Seq[(Job, Field)]] = db.run {
    (for {
      job   <- Jobs
      field <- job.field
    } yield (job, field)).result
  }

  override def find(id: Id[Job]): Future[Option[(Job, Field)]] = db.run {
    (for {
      job <- Jobs
      if job.id === id
      field <- job.field
    } yield (job, field)).result.headOption
  }
}
