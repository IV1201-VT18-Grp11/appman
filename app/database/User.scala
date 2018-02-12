package database

import PgProfile.api._

// case class works as a DTO, taking the required columns from the database
case class User(id: Id[User],
                username: String,
                password: String,
                firstname: String,
                surname: String,
                email: String)
    extends HasId {
  type Self   = User
  type IdType = Long
}

/**
  * SLICK scala code instead of raw SQL, allowing low coupling
  * Selecting the columns from the table
  */
class Users(tag: Tag) extends Table[User](tag, "users") {
  def id        = column[Id[User]]("id", O.PrimaryKey, O.AutoInc)
  def username  = column[String]("username")
  def password  = column[String]("password")
  def firstname = column[String]("firstname")
  def surname   = column[String]("surname")
  def email     = column[String]("email")

  /**
    * These columns make up the User object
    */
  override def * =
    (id, username, password, firstname, surname, email) <> (User.tupled, User.unapply)
}

/**
  * Refering to this query when we use the object Users
  */
object Users extends TableQuery[Users](new Users(_))
