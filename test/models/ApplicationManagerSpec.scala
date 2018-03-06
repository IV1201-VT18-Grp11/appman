package models

import database.{ApplicationCompetence, Id, Job, JobApplication}
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import utils.DbOneAppPerTest
import utils.TestData

class ApplicationManagerSpec
    extends PlaySpec
    with DbOneAppPerTest
    with Injecting {
  "calling allCompetenecs()" should {
    "list all known competences" in {
      await(inject[ApplicationManager].allCompetences()) must contain
        .allElementsOf(TestData.Competences.all)
    }
  }

  "calling create()" should {
    "save the application to the database" in {
      val from       = LocalDate.now()
      val to         = from.plus(1, ChronoUnit.YEARS)
      val appManager = inject[ApplicationManager]
      val id = await(
        appManager.create(TestData.Users.gyroGearloose.id,
                          TestData.JobListings.shoeCleaning.id,
                          "Blah blah blah something something blah blah",
                          Map(TestData.Competences.it.id -> 200.0f),
                          Seq(from                       -> to))
      )
      val (app, job, _) =
        await(appManager.find(id, TestData.Users.gyroGearloose)).value
      app.description.value mustBe "Blah blah blah something something blah blah"
      job.value mustBe TestData.JobListings.shoeCleaning
      await(appManager.applicationAvailabilities(id))
        .map(x => x.from -> x.to) must contain.only(Some(from) -> Some(to))
      await(appManager.applicationCompetences(id)).map(_._2) must contain.only(
        ApplicationCompetence(TestData.Competences.it.id, Some(200.0f), id)
      )
    }
  }

  "calling find()" when {
    "the application does not exist" should {
      "return None" in {
        await(
          inject[ApplicationManager].find(Id[JobApplication](-1),
                                          TestData.Users.gyroGearloose)
        ) mustBe empty
      }
    }

    "the user is another applicant" should {
      "return None" in {
        val appManager = inject[ApplicationManager]
        val id = await(
          appManager.create(TestData.Users.gyroGearloose.id,
                            TestData.JobListings.shoeCleaning.id,
                            "",
                            Map(),
                            Seq())
        )
        await(appManager.find(id, TestData.Users.donaldDuck)) mustBe None
      }
    }

    "the user is the applicant" should {
      "return the application" in {
        val appManager = inject[ApplicationManager]
        val id = await(
          appManager.create(TestData.Users.gyroGearloose.id,
                            TestData.JobListings.shoeCleaning.id,
                            "",
                            Map(),
                            Seq())
        )
        await(appManager.find(id, TestData.Users.gyroGearloose)).value._1.id mustBe id
      }
    }

    "the user is an employee" should {
      "return the application" in {
        val appManager = inject[ApplicationManager]
        val id = await(
          appManager.create(TestData.Users.gyroGearloose.id,
                            TestData.JobListings.shoeCleaning.id,
                            "",
                            Map(),
                            Seq())
        )
        await(appManager.find(id, TestData.Users.hueyDuck)).value._1.id mustBe id
      }
    }
  }

  "calling all()" when {
    "only other users have submitted applications" should {
      "return an empty Seq" in {
        val appManager = inject[ApplicationManager]
        await(
          appManager.create(TestData.Users.gyroGearloose.id,
                            TestData.JobListings.shoeCleaning.id,
                            "",
                            Map(),
                            Seq())
        )
        await(appManager.all(TestData.Users.donaldDuck)) mustBe empty
      }
    }

    "the user has made an application" should {
      "return the application" in {
        val appManager = inject[ApplicationManager]
        val id = await(
          appManager.create(TestData.Users.gyroGearloose.id,
                            TestData.JobListings.shoeCleaning.id,
                            "",
                            Map(),
                            Seq())
        )
        await(appManager.all(TestData.Users.gyroGearloose))
          .map(_._1.id) must contain.only(id)
      }
    }

    "the user is an employee" should {
      "return the application" in {
        val appManager = inject[ApplicationManager]
        val id = await(
          appManager.create(TestData.Users.gyroGearloose.id,
                            TestData.JobListings.shoeCleaning.id,
                            "",
                            Map(),
                            Seq())
        )
        await(appManager.all(TestData.Users.hueyDuck))
          .map(_._1.id) must contain.only(id)
      }
    }
  }

  "calling setStatus()" should {
    "update the status" in {
      val appManager = inject[ApplicationManager]
      val id = await(
        appManager.create(TestData.Users.gyroGearloose.id,
                          TestData.JobListings.shoeCleaning.id,
                          "",
                          Map(),
                          Seq())
      )
      await(appManager.find(id, TestData.Users.gyroGearloose)).value._1.accepted mustBe None
      await(appManager.setStatus(id, true))
      await(appManager.find(id, TestData.Users.gyroGearloose)).value._1.accepted mustBe Some(
        true
      )
    }
  }
}
