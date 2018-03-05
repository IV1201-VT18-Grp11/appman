package controllers

import models.UserManager
import org.scalatestplus.play._
import play.api.test._
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import utils.DbOneAppPerTest

class LoginControllerSpec extends PlaySpec with DbOneAppPerTest with Injecting {
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

  "trying to register" when {
    "the entered passwords are different" should {
      "ask the user to correct the error" in {
        val request = FakeRequest(routes.LoginController.doRegister(None))
          .withFormUrlEncodedBody("username"        -> "scrooge_mc_duck",
                                  "password"        -> "password",
                                  "confirmPassword" -> "p4ssw0rd",
                                  "firstname"       -> "Scrooge",
                                  "surname"         -> "McDuck",
                                  "email"           -> "scrooge@mcduck.net")
          .withCSRFToken
        val register = route(app, request).get

        status(register) mustBe BAD_REQUEST
        contentAsString(register) must include(
          "The passwords you have entered do not match"
        )
      }
    }
    "the information conflicts with an existing user" should {
      "ask the user to correct the error" in {
        val userManager = inject[UserManager]
        await(
          userManager.register(username = "scrooge_mc_duck",
                               password = "password",
                               firstname = "Scrooge",
                               surname = "McDuck",
                               email = "scrooge@mcduck.net")
        ) mustBe 'right
        val request = FakeRequest(routes.LoginController.doRegister(None))
          .withFormUrlEncodedBody("username"        -> "scrooge_mc_duck",
                                  "password"        -> "password",
                                  "confirmPassword" -> "password",
                                  "firstname"       -> "Scrooge",
                                  "surname"         -> "McDuck",
                                  "email"           -> "scrooge@mcduck.net")
          .withCSRFToken
        val register = route(app, request).get

        status(register) mustBe BAD_REQUEST
        contentAsString(register) must (
          include("The username is already taken")
            and include("The email is already taken")
        )
      }
    }
  }
}
