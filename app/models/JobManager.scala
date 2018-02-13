package models

import javax.inject.Inject

import com.google.inject.ImplementedBy
import database.PgProfile.api._
import database._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.Future

@ImplementedBy(classOf[DbJobManager])
trait JobManager {

  /**
    * We want to show 0 or more jobs, as a sequence
    */
  def jobListings(): Future[Seq[(Job, Field)]]

  def find(id: Id[Job]): Future[Option[(Job, Field)]]
}

class DbJobManager @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
) extends JobManager
    with HasDatabaseConfigProvider[PgProfile] {

  /**
    * @return and show the job lists
    */
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
