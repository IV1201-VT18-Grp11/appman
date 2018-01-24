package database

import PgProfile.api._

case class User(id: Long,
                username: String,
                password: String,
                name: Option[String])

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("users_id", O.PrimaryKey, O.AutoInc)
  def username = column[String] ("username")
  def password = column[String] ("password")
  def name = column[Option[String]] ("name")

  // These columns make up the User object
  override def * = (id, username, password, name) <> (User.tupled, User.unapply)
}
