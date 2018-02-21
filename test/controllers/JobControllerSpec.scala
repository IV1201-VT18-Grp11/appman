package controllers

import database.{Id, Job, JobApplication}
import models.ApplicationManager
import org.scalatestplus.play.PlaySpec
import play.api.http.HttpErrorHandler
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._
import utils.DbOneAppPerTest

import scala.concurrent.ExecutionContext.Implicits.global

class JobControllerSpec extends PlaySpec with DbOneAppPerTest with Injecting {
  "trying to view the available job list" should {
    "show all available jobs" in {
      val request = FakeRequest(routes.JobController.jobList())
      val page    = route(app, request).get

      status(page) mustBe OK
      contentAsString(page) must (
        include("Scala Magician")
          and include(routes.JobController.jobDescription(Id[Job](1)).url)
      )
    }
  }

  "trying to view a job listing" when {
    "the job exists" should {
      "show the job details" in {
        val request =
          FakeRequest(routes.JobController.jobDescription(Id[Job](1)))
        val page = route(app, request).get

        status(page) mustBe OK
        contentAsString(page) must (
          include("Scala Magician")                                // Name
            and include("2018-10-01")                              // End date
            and include("Sweden")                                  // Country
            and include("Wonderful experience that will make you") // Description
            and include("Inner beauty.")                           // Requirements
            and include("Apply")                                   // Apply link
        )
      }
    }

    "the ID is invalid" should {
      "cause a 404 error" in {
        val request =
          FakeRequest(routes.JobController.jobDescription(Id[Job](-1)))
        val errorHandler = inject[HttpErrorHandler]
        val page = route(app, request).get
          .recoverWith {
            case ex => errorHandler.onServerError(request, ex)
          }

        status(page) mustBe NOT_FOUND
      }
    }
  }

  "trying to apply for a job" should {
    "show the application form" in {
      val request =
        FakeRequest(routes.JobController.applyForJob(Id[Job](1)))
      val page = route(app, request).get

      status(page) mustBe SEE_OTHER
      val infoPage =
        route(app, FakeRequest("GET", redirectLocation(page).get)).get

      status(infoPage) mustBe OK
      contentAsString(page) must (
        include("Apply to be a Scala Magician")
          and include("Why are you a good fit for this job")
          and include("Competences")
          and include("Years of Experience")
          and include("IT")
          and include("Cooking")
          and include("From")
          and include("To")
          and include("Avialability begins at")
          and include("until")
          and include("Apply")
      )
    }
  }

  "trying to send a application" should {
    "show the application details" in {
      val request =
        FakeRequest(
          routes.JobController.applicationDescription(Id[Job](1),
                                                      Id[JobApplication](1))
        )
      val page = route(app, request).get

      status(page) mustBe SEE_OTHER
      val infoPage =
        route(app, FakeRequest("GET", redirectLocation(page).get)).get

      status(infoPage) mustBe OK
      contentAsString(infoPage) must (
        include("Application number")
          and include("Full Name")
          and include("Teo Klestrup")
          and include("Username")
          and include("laserkitten")
          and include("E-mail")
          and include("teo@nullable.se")
          and include("Job")
          and include("Scala Magician")
          and include("Availabilities")
          and include("2018-01-01")
          and include("Date of Application")
          and include("Web, 21 Feb 2018")
          and include("Applicant Description")
          and include("I am awesome")
          and include("Applicant Competence")
          and include("IT (11.0 years")
      )
    }
  }
}
