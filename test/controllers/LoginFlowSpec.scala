package controllers

import models.UserManager
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import utils.DbOneServerPerTest

class LoginFlowSpec
    extends PlaySpec
    with DbOneServerPerTest
    with OneBrowserPerTest
    with FirefoxFactory
    with Injecting {
  "login should work" in {
    val userManager = inject[UserManager]
    await(
      userManager.register("scrooge",
                           "money",
                           "Scrooge",
                           "McDuck",
                           "scrooge@kittens.org")
    )

    go to (s"http://localhost:$port/")
    click on find(id("nav-login")).value

    textField(name("username")).value = "scrooge"
    pwdField(name("password")).value = "moolah"
    click on find(id("login")).value
    find(id("message")).fold("")(_.text) mustNot include(
      "You have been logged in"
    )
    find(tagName("body")).value.text must include(
      "Invalid username or password"
    )

    textField(name("username")).value = "scrooge"
    pwdField(name("password")).value = "money"
    click on find(id("login")).value
    find(id("message")).value.text must include("You have been logged in")
    find(id("nav-login")) mustBe empty

    click on find(id("nav-logout")).value
    find(id("message")).value.text must include("You have been logged out")
    find(id("nav-login")) mustBe defined
    find(id("nav-logout")) mustBe empty
  }
}
