package controllers

import models.UserManager
import org.scalatestplus.play._
import play.api.test._
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import utils.DbOneAppPerTest

class LoginControllerSpec extends PlaySpec with DbOneAppPerTest with Injecting {
  override def fakeApplication() = {
    val app         = super.fakeApplication()
    val userManager = app.injector.instanceOf[UserManager]
    await(
      userManager.register("gyro_gearloose",
                           "little_helper",
                           "Gyro",
                           "Gearloose",
                           "gyro@duck.net")
    ) mustBe 'right
    app
  }

  "trying to log in" when {
    "the username or password is incorrect" should {
      "ask the user to try again" in {
        val request = FakeRequest(routes.LoginController.doLogin(None))
          .withFormUrlEncodedBody("username" -> "scrooge_mc_duck",
                                  "password" -> "password")
          .withCSRFToken
        val login = route(app, request).get

        status(login) mustBe BAD_REQUEST
        contentAsString(login) must include("Invalid username or password")
      }
    }

    "the username and password are correct" should {
      "redirect the user to the home page" in {
        val request = FakeRequest(routes.LoginController.doLogin(None))
          .withFormUrlEncodedBody("username" -> "gyro_gearloose",
                                  "password" -> "little_helper")
          .withCSRFToken
        val login = route(app, request).get

        session(login).get(Security.sessionKey) mustBe defined
        redirectLocation(login) mustBe defined
      }
    }
  }
}
