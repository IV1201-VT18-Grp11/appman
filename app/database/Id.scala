package database

case class Id[A <: HasId](raw: A#IdType)

trait HasId {
  def id: Id[Self]

  type Self >: this.type <: HasId
  type IdType
}
