package utils

import database.{Competence, Field, Id, Job, Role, User}
import java.time.Instant

object TestData {
  object Users {
    val gyroGearloose = User(Id[User](3),
                             Some("gyro_gearloose"),
                             Some(
                               "abd75495efc7b846a4ba75a61a11906d4b39ce9f1dea960ee1e76e2edae0f1c9&47cdeb999fc6b61e3c4d96e34a1021aced2524bb2517c66e6374a1997bb9d04f6d8e4c76133e107e5cf70df447336abbafd470723cee49f5dd6a6de157f75507"
                             ), // little_helper
                             Some("Gyro"),
                             Some("Gearloose"),
                             Some("gyro@gearloo.se"))
    val donaldDuck = User(Id[User](4),
                          Some("donald_duck"),
                          Some(
                            "48d87a99f85058309d8bba9f27862009148187094350cd7eb49fadee280ce776&5acc98a4390449bf1707d85d07e7c8b3334a5739da072385583abf8f6454bbba1dc7d37ae1936217c5c6f615f7525ac49cd0a5b85b2430d5f5443789a1297206"
                          ), // 123456
                          Some("Donald"),
                          Some("Duck"),
                          Some("donald@duck.net"))
    val hueyDuck = User(Id[User](5),
                        Some("huey_duck"),
                        Some(
                          "48d87a99f85058309d8bba9f27862009148187094350cd7eb49fadee280ce776&5acc98a4390449bf1707d85d07e7c8b3334a5739da072385583abf8f6454bbba1dc7d37ae1936217c5c6f615f7525ac49cd0a5b85b2430d5f5443789a1297206"
                        ), // 123456
                        Some("Huey"),
                        Some("Duck"),
                        Some("notdonald@duck.net"),
                        role = Role.Employee)
    val all = Seq(gyroGearloose, donaldDuck, hueyDuck)
  }

  object Competences {
    val it      = Competence(Id[Competence](500), Some("IT"))
    val cooking = Competence(Id[Competence](650), Some("Cooking"))

    val all = Seq(it, cooking)
  }

  object Fields {
    val hospitality = Field(Id[Field](500), "Hospitality")
    val music       = Field(Id[Field](1011001), "Music")

    val all = Seq(hospitality, music)
  }

  object JobListings {
    val shoeCleaning = Job(Id[Job](100),
                           Fields.hospitality.id,
                           name = "Shoe cleaning",
                           fromDate = Instant.now(),
                           toDate = None,
                           country = None,
                           description =
                             "Do you love cleaning shoes? Apply here!",
                           requirement = "Competence")
    val musician = Job(Id[Job](101),
                       Fields.music.id,
                       name = "Musician",
                       fromDate = Instant.now(),
                       toDate = None,
                       country = None,
                       description = "If you need to ask then you're not it",
                       requirement = "Scales")

    val all = Seq(shoeCleaning, musician)
  }
}
