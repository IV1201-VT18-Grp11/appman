package database

import PgProfile.api._
import java.time.Instant

case class Session(id: Id[Session],
                   user: Id[User],
                   from: Instant,
                   refreshed: Instant,
                   deleted: Boolean) extends HasId {
  type Self = Session
  type IdType = Long
}

class Sessions(tag: Tag) extends Table[Session](tag, "sessions") {
  def id = column[Id[Session]]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Id[User]]("user")
  def from = column[Instant]("from")
  def refreshed = column[Instant]("refreshed")
  def deleted = column[Boolean]("deleted")

  override def * = (id, userId, from, refreshed, deleted) <> (Session.tupled, Session.unapply)
}

object Sessions extends TableQuery[Sessions](new Sessions(_))
