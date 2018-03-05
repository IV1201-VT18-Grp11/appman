package models

import database.{Id, Job}
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import utils.{DbOneAppPerTest, TestData}

class JobManagerSpec extends PlaySpec with DbOneAppPerTest with Injecting {
  "calling jobListings()" should {
    "list all available jobs" in {
      await(inject[JobManager].jobListings()).map(_._1) must contain
        .allElementsOf(TestData.JobListings.all)
    }
  }

  "calling find()" when {
    "the listing exists" should {
      "return the job" in {
        val (job, field) = await(
          inject[JobManager].find(TestData.JobListings.shoeCleaning.id)
        ).value
        job mustBe TestData.JobListings.shoeCleaning
        field.id mustBe job.fieldId
      }
    }

    "no such listing exists" should {
      "return None" in {
        await(inject[JobManager].find(Id[Job](-1))) mustBe empty
      }
    }
  }
}
