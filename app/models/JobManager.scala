import javax.inject.Inject

import database.PgProfile.api._
import database._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import scala.concurrent.{ExecutionContext, Future}

trait JobManager {
  /**
    * We want to show 0 or more jobs, as a sequence
    */
  def jobListings()(implicit ec: ExecutionContext): Future[Seq[Job]]
}

class DbJobManager @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends JobManager with HasDatabaseConfigProvider[PgProfile] {

  /**
    * @return and show the job lists
    */
  override def jobListings()(implicit ec: ExecutionContext): Future[Seq[Job]] = db.run {
    Jobs.result
  }
}