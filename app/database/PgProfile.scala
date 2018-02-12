package database

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support}

trait PgProfile extends ExPostgresProfile with PgDate2Support {
  trait API extends super.API with DateTimeImplicits {
    implicit def idColumnType[T <: HasId](
      implicit raw: ColumnType[T#IdType]
    ): ColumnType[Id[T]] = MappedColumnType.base[Id[T], T#IdType](_.raw, Id(_))
  }

  override val api = new API {}
}

object PgProfile extends PgProfile
