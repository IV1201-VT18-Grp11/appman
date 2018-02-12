package database

import utils.Enum

sealed trait Role extends Ordered[Role] {
  def compare(that: Role): Int =
    Role.values.indexOf(this) - Role.values.indexOf(that)
}
sealed trait UserRole extends Role
object Role extends Enum[Role] {
  case object Anonymous extends Role
  case object Applicant extends UserRole
  case object Employee  extends UserRole
  case object Admin     extends UserRole
  override def values = Seq(Anonymous) ++ UserRole.values
}
object UserRole extends Enum[UserRole] {
  override def values = Seq(Role.Applicant, Role.Employee, Role.Admin)
}
