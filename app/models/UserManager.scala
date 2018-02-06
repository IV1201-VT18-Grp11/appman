package models

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

import com.google.inject.ImplementedBy
import database.PgProfile.api._
import database._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{ Failure, Success }

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
  def register(username: String, password: String, firstname: String, surname: String, email: String)(implicit ec: ExecutionContext): Future[Either[Seq[UserManager.RegistrationError], User]]
}

object UserManager {
  sealed trait RegistrationError
  object RegistrationError {
    case object UsernameTaken extends RegistrationError
    case object EmailTaken extends RegistrationError
  }
}

class DbUserManager @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                              passwordHasher: PasswordHasher) extends UserManager with HasDatabaseConfigProvider[PgProfile] {
  private val logger = Logger(getClass)

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

  override def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[UserSession]] = {
    val task = db.run {
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
    task.foreach {
      case Some(session) =>
        logger.info(s"User $username logged in with session ${session.id.raw}")
      case None =>
        logger.info(s"User $username failed to log in")
    }
    task
  }

  override def register(username: String, password: String, firstname: String, surname: String, email: String)(implicit ec: ExecutionContext): Future[Either[Seq[UserManager.RegistrationError], User]] = {
    val task = db.run {
      (for {
         userId <- Users.returning(Users.map(_.id)) += User(Id[User](-1),
                                                            username = username,
                                                            password = passwordHasher.hash(password),
                                                            firstname = firstname,
                                                            surname = surname,
                                                            email = email)
         user <- Users.filter(_.id === userId).result.head
       } yield user).transactionally.asTry.flatMap {
        case Success(user) =>
          DBIO.successful(Right(user))
        case Failure(_) =>
          DBIO.sequence(
            Seq(
              Users.filter(_.username === username).result.headOption.map(_.map(_ => UserManager.RegistrationError.UsernameTaken)),
              Users.filter(_.email === email).result.headOption.map(_.map(_ => UserManager.RegistrationError.EmailTaken))
            )
          )
            .map(_.flatten)
            .map(Left.apply)
      }
    }
    task.foreach {
      case Right(user) =>
        logger.info(s"User $username was successfully created, with id ${user.id.raw}")
      case Left(reason) =>
        logger.info(s"Failed to create $username: $reason")
    }
    task
  }
}
