package models

import java.sql.SQLException
import org.scalatestplus.play.{
  BaseOneAppPerTest,
  FakeApplicationFactory,
  PlaySpec
}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.Configuration
import play.api.Application
import play.api.test.Injecting
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global

class DbFailureSpec
    extends PlaySpec
    with FakeApplicationFactory
    with BaseOneAppPerTest
    with Injecting {
  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .loadConfig(
        env =>
          Configuration
            .load(env,
                  Map("slick.dbs.default.db.schema"         -> "does_not_exist",
                      "slick.dbs.default.db.connectionPool" -> "disabled",
                      "play.evolutions.db.default.enabled"  -> "false"))
      )
      .build
  }

  "calling UserManager.login()" when {
    "the database is unavailable" should {
      "throw an execption" in {
        val userManager = inject[UserManager]
        an[SQLException] should be thrownBy await(
          userManager.login("donald_duck", "123456")
        )
      }
    }
  }

  "calling UserManager.register()" when {
    "the database is unavailable" should {
      "throw an exception" in {
        val userManager = inject[UserManager]
        an[SQLException] should be thrownBy await(
          userManager.register("to_be_created",
                               "1234",
                               "Laser",
                               "Kitten",
                               "kitten1@kittens.org")
        )
      }
    }
  }
}
