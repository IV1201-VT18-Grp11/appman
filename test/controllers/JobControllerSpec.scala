package controllers

import database.{Id, Job, JobApplication}
import models.{ApplicationManager, UserManager}
import org.scalatestplus.play.PlaySpec
import play.api.http.HttpErrorHandler
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
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
      val userManager = inject[UserManager]
      val session = await(
        userManager.register("foobar", "empty", "Foo", "Bar", "foo@bar.com")
      ).right.get

      val request =
        FakeRequest(routes.JobController.applyForJob(Id[Job](1)))
          .withSession(Security.sessionKey -> session.id.raw.toString)
      val page = route(app, request).get
      status(page) mustBe OK
      contentAsString(page) must (
        include("Apply to be a Scala Magician")
          and include("Why are you a good fit for this job")
          and include("Competences")
          and include("Years of Experience")
          and include("IT")
          and include("Cooking")
          and include("From")
          and include("To")
          and include("Availability begins at")
          and include("until")
          and include("Apply")
      )
    }
  }

  "trying to send an application" should {
    "show the application details" in {
      val userManager = inject[UserManager]
      val session = await(
        userManager.register("foobar", "empty", "Foo", "Bar", "foo@bar.com")
      ).right.get

      val request =
        FakeRequest(routes.JobController.doApplyForJob(Id[Job](1)))
          .withSession(Security.sessionKey -> session.id.raw.toString)
          .withFormUrlEncodedBody("description"                    -> "I am good at...",
                                  "competences[0].id"              -> "1",
                                  "competences[0].experienceYears" -> "5",
                                  "competences[1].id"              -> "2",
                                  "competences[1].experienceYears" -> "0",
                                  "availabilities[0].from"         -> "2018-02-01",
                                  "availabilities[0].to"           -> "2018-06-05")
          .withCSRFToken
      val page = route(app, request).get

      status(page) mustBe SEE_OTHER
      val infoPage =
        route(
          app,
          FakeRequest("GET", redirectLocation(page).get)
            .withSession(Security.sessionKey -> session.id.raw.toString)
        ).get

      status(infoPage) mustBe OK
      contentAsString(infoPage) must (
        include("Application number")
          and include("Foo Bar")
          and include("foobar")
          and include("foo@bar.com")
          and include("Scala Magician")
          and include("2018-02-01")
          and include("2018-06-05")
          and include("I am good at")
          and include("IT")
          and include("5.0 years")
      )
    }
  }
}
