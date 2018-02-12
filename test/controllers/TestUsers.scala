package controllers

import database.{Id, User}

object TestUsers {
  val gyroGearloose = User(Id[User](3),
                           "gyro_gearloose",
                           "little_helper",
                           "Mad",
                           "Kitten",
                           "kitten4@kittens.org")
}
