package controllers

import database.{ Id, User, UserSession }
import java.time.Instant
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import models.UserManager
import org.scalatestplus.play._
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.{ AbstractController, ActionBuilder, AnyContent, BaseController, ControllerComponents, Request, Results }
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
        security.getSessionId(request) mustBe None
        await(security.findUser(request)) mustBe None
      }
    }

    "there is a session for a user" should {
      "return the session's ID" in {
        val security = new FakeSecurity()
        when(security.userManager.findSession(Id[UserSession](4)))
          .thenReturn(Future.successful(Some((TestUsers.gyroGearloose,
                                              UserSession(
                                                Id[UserSession](4),
                                                TestUsers.gyroGearloose.id,
                                                from = Instant.now(),
                                                refreshed = Instant.now(),
                                                deleted = false,
                                              )))))

        val loginResponse = security.setUserSessionId(Results.Ok(""), FakeRequest(), Id[UserSession](4))
        val request = FakeRequest().withSession(loginResponse.newSession.get.data.toSeq: _*)
        security.getSessionId(request).value mustBe Id[UserSession](4)
        await(security.findUser(request)).value._1.username mustBe "gyro_gearloose"
      }
    }
  }
}
