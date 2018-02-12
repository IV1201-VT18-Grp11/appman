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
    val app = new GuiceApplicationBuilder()
    // .disable[EvolutionsModule]
      .loadConfig(
        env =>
          Configuration
            .load(env,
                  Map("slick.dbs.default.db.connectionPool" -> "disabled",
                      "play.evolutions.db.default.enabled"  -> "false"))
      )
      .build
    // app.injector.instanceOf[DatabaseConfigProvider].get.db.close()
    app
  }

  "calling UserManager.login()" when {
    "the database is unavailable" should {
      "throw an execption" in {
        val userManager = inject[UserManager]
        an[SQLException] should be thrownBy await(
          inject[UserManager].login("donald_duck", "123456")
        )
      }
    }
  }
}
