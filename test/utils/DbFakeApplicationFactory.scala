package utils

import database.PgProfile
import database.PgProfile.api._
import org.scalatest._
import org.scalatestplus.play._
import play.api.{ Application, Configuration }
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.ApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder
import scala.util.Random
import slick.basic.DatabaseConfig
import play.api.test.Helpers._


trait DbFakeApplicationFactory extends FakeApplicationFactory { this: TestSuite =>
  def fakeApplicationWithRealDb(): Application = new GuiceApplicationBuilder().build()

  override def fakeApplication() = {
    val schema = s"test_${Math.abs(Random.self.nextLong())}"
    val app = fakeApplicationWithRealDb()
    val dbProvider = app.injector.instanceOf[DatabaseConfigProvider]
    val db = dbProvider.get[PgProfile].db
    await(db.run(sql"CREATE SCHEMA #$schema".asUpdate))
    app.stop()
    val appWithDb =
      new GuiceApplicationBuilder()
        .loadConfig(env => Configuration.load(env, Map("slick.dbs.default.db.schema" -> schema)))
        .build()
    appWithDb.injector.instanceOf[ApplicationLifecycle].addStopHook { () =>
      appWithDb.injector.instanceOf[DatabaseConfigProvider]
        .get[PgProfile]
        .db.run(sql"DROP SCHEMA #$schema CASCADE".asUpdate)
    }
    appWithDb
  }
}
