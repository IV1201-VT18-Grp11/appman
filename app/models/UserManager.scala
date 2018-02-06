package models

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

import com.google.inject.ImplementedBy
import database.PgProfile.api._
import database._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DbUserManager])
trait UserManager {
  /**
    * Finds the user with a given ID
    *
    * @return Some(user) if the user exists, otherwise None
    */
  def find(id: Id[User]): Future[Option[User]]

  def findSession(id: Id[UserSession])(implicit ec: ExecutionContext): Future[Option[(User, UserSession)]]

  /**
    * Finds the user with a given username and password and creates a session
    *
    * @return Some(session) if the user exists and the password is correct, otherwise None
    */
  def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[UserSession]]

  /**
    * Tries to create a user with the given fields
    *
    * @return None if a user with the given username already exists, otherwise Some(user)
    */
  def register(username: String, password: String, firstname: String, surname: String, email: String)(implicit ec: ExecutionContext): Future[Option[User]]
}

class DbUserManager @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                              passwordHasher: PasswordHasher) extends UserManager with HasDatabaseConfigProvider[PgProfile] {
  override def find(id: Id[User]): Future[Option[User]] = db.run {
    Users.filter(_.id === id)
      .result.headOption
  }

  override def findSession(id: Id[UserSession])(implicit ec: ExecutionContext): Future[Option[(User, UserSession)]] = db.run {
    for {
      session <- (for {
        session <- UserSessions
        if session.id === id
        if !session.deleted
        if session.refreshed > Instant.now().minus(1, ChronoUnit.DAYS)
        user <- session.user
      } yield (user, session)).result.headOption
      _ <- UserSessions
        .filter(_.id === session.map(_._2.id))
        .map(_.refreshed)
        .update(Instant.now())
    } yield session
  }

  override def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[UserSession]] = db.run {
    (for {
      user <- Users
        .filter(user => user.username === username)
        .result.head
      if passwordHasher.compare(user.password, password)
      session <- UserSessions
        .map(_.userId)
        .returning(UserSessions) += user.id
    } yield session).asTry.map(_.toOption)
  }

  override def register(username: String, password: String, firstname: String, surname: String, email: String)(implicit ec: ExecutionContext): Future[Option[User]] = db.run {
    (for {
      userId <- Users.returning(Users.map(_.id)) += User(Id[User](-1),
        username = username,
        password = passwordHasher.hash(password),
        firstname = firstname,
        surname = surname,
        email = email)
      user <- Users.filter(_.id === userId).result.head
    } yield user).transactionally.asTry.map(_.toOption)
  }
}
