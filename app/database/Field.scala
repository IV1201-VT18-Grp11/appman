package database

import PgProfile.api._

case class Field(id: Id[Field], name: String) extends HasId {
  type Self   = Field
  type IdType = Long
}

class Fields(tag: Tag) extends Table[Field](tag, "fields") {
  def id   = column[Id[Field]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  /**
    *
    */
  override def * = (id, name) <> (Field.tupled, Field.unapply)
}

/**
  *
  */
object Fields extends TableQuery[Fields](new Fields(_))
