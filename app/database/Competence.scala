package database

import database.PgProfile.api._

/**
  * A skill that an applicant may or may not have.
  */
case class Competence(id: Id[Competence], name: Option[String]) extends HasId {
  type Self   = Competence
  type IdType = Long
}

class Competences(tag: Tag) extends Table[Competence](tag, "competences") {
  def id   = column[Id[Competence]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[Option[String]]("name")

  override def * = (id, name) <> (Competence.tupled, Competence.unapply)
}

object Competences extends TableQuery[Competences](new Competences(_))
