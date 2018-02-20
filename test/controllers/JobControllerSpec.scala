package controllers

import database.{Id, Job}
import models.ApplicationManager
import org.scalatestplus.play.PlaySpec
import play.api.http.HttpErrorHandler
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._
import utils.DbOneAppPerTest
import scala.concurrent.ExecutionContext.Implicits.global

class JobControllerSpec extends PlaySpec with DbOneAppPerTest with Injecting {
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
}
