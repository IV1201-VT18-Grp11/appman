package models

import database.{ Id, User }
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import utils.DbOneAppPerTest

class UserManagerSpec extends PlaySpec with DbOneAppPerTest with Injecting {
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
        await(inject[UserManager].login("donald_duck", "123456")).map(_.user) mustBe Some(Id[User](2))
      }
    }
  }

  "calling register()" when {
    "the username is not taken" should {
      "return the user" in {
        await(inject[UserManager].register("to_be_created", "1234", "Laser", "Kitten", "kitten1@kittens.org")).value.username mustBe "to_be_created"
      }
    }

    "the username is already taken" should {
      "return None" in {
        await(inject[UserManager].register("to_be_created", "1234", "Laser", "Kitten", "kitten1@kittens.org")) mustBe defined // unique user
        await(inject[UserManager].register("to_be_created", "1234", "Crazy", "Kitten", "kitten2@kittens.org")) mustBe None // let's not accept another user with the same username
        await(inject[UserManager].register("to_be_created2", "1234", "Cute", "Kitten", "kitten1@kittens.org")) mustBe None // let's not accept another user with the same email
      }
    }
  }
}
