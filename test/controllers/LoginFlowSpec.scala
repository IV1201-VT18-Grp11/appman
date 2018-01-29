package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import utils.DbOneServerPerTest

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class LoginFlowSpec extends PlaySpec with DbOneServerPerTest with OneBrowserPerTest with FirefoxFactory with Injecting {
  "login should work" in {
    go to (s"http://localhost:$port/")
    click on find(id("nav-login")).value
    textField(name("username")).value = "donald_duck"
    pwdField(name("password")).value = "123456"
    click on find(id("login")).value
    eventually { find(id("message")).value.text must include("You have been logged in") }
  }
}
