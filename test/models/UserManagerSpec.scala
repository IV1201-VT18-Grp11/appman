package models

import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import utils.{DbOneAppPerTest, TestData}

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
      "create a session" in {
        val userManager = inject[UserManager]
        val session     = await(userManager.login("donald_duck", "123456")).value
        session.user mustBe TestData.Users.donaldDuck.id
        await(userManager.findSession(session.id)).value._2 mustBe session
      }
    }
  }

  "calling register()" when {
    "the username is not taken" should {
      "return the user" in {
        val userManager = inject[UserManager]
        val session = await(
          userManager.register("to_be_created",
                               "1234",
                               "Laser",
                               "Kitten",
                               "kitten1@kittens.org")
        ).right.get
        await(userManager.find(session.user)).value.username.value mustBe "to_be_created"
      }
    }

    "the username is already taken" should {
      "return None" in {
        await(
          inject[UserManager].register("to_be_created",
                                       "1234",
                                       "Laser",
                                       "Kitten",
                                       "kitten1@kittens.org")
        ) mustBe 'right // unique user
        await(
          inject[UserManager].register("to_be_created",
                                       "1234",
                                       "Crazy",
                                       "Kitten",
                                       "kitten2@kittens.org")
        ) mustBe Left(Seq(UserManager.RegistrationError.UsernameTaken))
        await(
          inject[UserManager].register("to_be_created2",
                                       "1234",
                                       "Cute",
                                       "Kitten",
                                       "kitten1@kittens.org")
        ) mustBe Left(Seq(UserManager.RegistrationError.EmailTaken))
      }
    }
  }
}
