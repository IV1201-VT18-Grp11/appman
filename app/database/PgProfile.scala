package database

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support}


class PgProfile extends ExPostgresProfile
  with PgDate2Support {

  trait API extends super.API with DateTimeImplicits

  object API extends API

}

object PgProfile extends PgProfile
