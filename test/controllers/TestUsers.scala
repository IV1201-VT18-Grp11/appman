package controllers

import database.{Id, User}

object TestUsers {
  val gyroGearloose = User(Id[User](3),
                           Some("gyro_gearloose"),
                           Some("little_helper"),
                           Some("Mad"),
                           Some("Kitten"),
                           Some("kitten4@kittens.org"))
}
