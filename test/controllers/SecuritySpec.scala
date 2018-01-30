package controllers

import database.{ Id, User }
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import models.UserManager
import org.scalatestplus.play._
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.{ AbstractController, ActionBuilder, AnyContent, BaseController, ControllerComponents, Request, Results }
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Future

class SecuritySpec extends PlaySpec with MockitoSugar {
  private class FakeSecurity extends Security {
    override val userManager = mock[UserManager]
    override val Action = mock[ActionBuilder[Request, AnyContent]]
  }

  "getUserId" when {
    "there is no current session" should {
      "return None" in {
        val security = new FakeSecurity()
        val request = FakeRequest()
        security.getUserId(request) mustBe None
        await(security.findUser(request)) mustBe None
      }
    }

    "there is a session for a user" should {
      "return the user's id" in {
        val security = new FakeSecurity()
        when(security.userManager.find(Id[User](4)))
          .thenReturn(Future.successful(Some(TestUsers.gyroGearloose)))

        val loginResponse = security.setUserId(Results.Ok(""), FakeRequest(), Id[User](4))
        val request = FakeRequest().withSession(loginResponse.newSession.get.data.toSeq: _*)
        security.getUserId(request).value mustBe Id[User](4)
        await(security.findUser(request)).value.username mustBe "gyro_gearloose"
      }
    }
  }
}
