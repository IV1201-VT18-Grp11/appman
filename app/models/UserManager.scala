package models

import javax.inject.Inject

import com.google.inject.ImplementedBy
import database.PgProfile.api._
import database.{PgProfile, User, Users}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[DbUserManager])
trait UserManager {
  /**
    * Finds the user with a given ID
    *  @return Some(user) if the user exists, otherwise None
    */
  def find(id: Long): Future[Option[User]]

  /**
    * Finds the user with a given username and password
    * @return Some(user) if the user exists and the password is correct, otherwise None
    */
  def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[User]]

  def register(username: String, password: String): Future[Option[User]]
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

  override def register(username: String, password: String): Future[Option[User]] = ???
}
