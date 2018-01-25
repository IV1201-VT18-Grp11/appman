package models

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class UserManagerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "calling login()" when {
    "the username does not exist" should {
      "return None" in {
        await(inject[UserManager].login("does_not_exist", "1234")) mustBe None
      }
    }
    "the password is incorrect" should {
      "return None" in {
        await(inject[UserManager].login("donald_duck", "1234")) mustBe None
      }
    }
    "the username and password are correct" should {
      "return the user" in {
        await(inject[UserManager].login("donald_duck", "123456")).map(_.username) mustBe Some("donald_duck")
      }
    }
  }
}
