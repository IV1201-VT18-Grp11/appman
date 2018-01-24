package models

import javax.inject.Inject

import com.google.inject.ImplementedBy
import database.PgProfile.api._
import database.{PgProfile, User, Users}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[DbUserManager])
trait UserManager {
  def find(id: Long): Future[Option[User]]
  def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[User]]
}

class DbUserManager @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserManager with HasDatabaseConfigProvider[PgProfile] {
  // TODO: Replace with better hashing algorithm
  private def hashPassword(plaintext: String): String = plaintext
  private def comparePassword(hashed: String, plaintext: String): Boolean = hashed == plaintext

  override def find(id: Long): Future[Option[User]] = ???

  override def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[User]] = db.run {
    Users
      .filter(user => user.username === username)
      .result.headOption
      .map(maybeUser => maybeUser.filter(user => comparePassword(user.password, password)))
  }
}
