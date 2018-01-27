package controllers

import database.{ Id, User }
import models.UserManager
import org.mockito.ArgumentMatchers.{eq => equ, _} // `eq` is already reserved by Scala
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.inject._
import play.api.inject.guice._
import play.api.mvc.AnyContent
import play.api.test._
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.DbOneAppPerTest

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class LoginControllerSpec extends PlaySpec with DbOneAppPerTest with Injecting with MockitoSugar {
  override def fakeApplication() =
    new GuiceApplicationBuilder()
      .overrides(Seq(
                   bind[UserManager].toInstance {
                     val manager = mock[UserManager]
                     when(manager.login(anyString, anyString)(any[ExecutionContext]))
                       .thenReturn(Future.successful(None))
                     when(manager.login(equ("gyro_gearloose"), equ("little_helper"))(any[ExecutionContext]))
                       .thenReturn(Future.successful(Some(TestUsers.gyroGearloose)))
                     manager
                   }
                 ))
      .build

  "trying to log in" when {
    "the username or password is incorrect" should {
      "ask the user to try again" in {
        val request = FakeRequest(routes.LoginController.doLogin())
         .withFormUrlEncodedBody(
            "username" -> "scrooge_mc_duck",
            "password" -> "password"
          )
          .withCSRFToken
        val login = route(app, request).get

        status(login) mustBe BAD_REQUEST
        contentAsString(login) must include("Invalid username or password")
      }
    }

    "the username and password are correct" should {
      "redirect the user to the home page" in {
        val request = FakeRequest(routes.LoginController.doLogin())
          .withCSRFToken
          .withBody(Map(
                      "username" -> Seq("gyro_gearloose"),
                      "password" -> Seq("little_helper")
                    ))
        val login = route(app, request).get

        session(login).get(Security.sessionKey).value mustBe "3"
        redirectLocation(login) mustBe defined
      }
    }
  }
}
