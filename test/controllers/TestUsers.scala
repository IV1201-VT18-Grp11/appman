package controllers

import database.{Id, User}

object TestUsers {
  val gyroGearloose = Users(Id[User](3),
                           "gyro_gearloose",
                           "little_helper",
                           "Mad",
                           "Kitten",
                           "kitten4@kittens.org")
}
