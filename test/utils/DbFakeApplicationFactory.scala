package utils

import database.{Competences, PgProfile, Users}
import database.PgProfile.api._
import org.scalatest._
import org.scalatestplus.play._
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.ApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder
import scala.util.Random
import play.api.test.Helpers._
import slick.basic.DatabaseConfig

import scala.concurrent.ExecutionContext.Implicits.global

trait DbFakeApplicationFactory extends FakeApplicationFactory {
  this: TestSuite =>
  override def fakeApplication() = {
    val schema = s"test_${Math.abs(Random.self.nextLong())}"
    val db     = DatabaseConfig.forConfig[PgProfile]("slick.dbs.default").db
    try {
      await(db.run(sql"CREATE SCHEMA #$schema".asUpdate))
    } finally {
      db.close()
    }
    val app =
      new GuiceApplicationBuilder()
        .loadConfig(
          env =>
            Configuration.load(env,
                               Map("slick.dbs.default.db.schema" -> schema))
        )
        .build()
    val appDb = app.injector
      .instanceOf[DatabaseConfigProvider]
      .get[PgProfile]
      .db
    app.injector.instanceOf[ApplicationLifecycle].addStopHook { () =>
      appDb.run(sql"DROP SCHEMA #$schema CASCADE".asUpdate)
    }
    await(appDb.run(for {
      _ <- Users.forceInsertAll(TestData.Users.all)
      _ <- Competences.forceInsertAll(TestData.Competences.all)
    } yield ()))
    app
  }
}
