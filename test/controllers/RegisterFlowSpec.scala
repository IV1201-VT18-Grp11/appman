package controllers

import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerTest, PlaySpec}
import play.api.test.Injecting
import utils.DbOneServerPerTest


  class RegisterFlowSpec extends PlaySpec with DbOneServerPerTest with OneBrowserPerTest with FirefoxFactory with Injecting {
    "register should work" in {
      pending
      go to (s"http://localhost:$port/")
      click on find(id("nav-register")).value
      textField(name("username")).value = "donald_duck"
      pwdField(name("password")).value = "123456"
      click on find(id("register")).value
      eventually { find(id("messages")).value.text must include("You have been registered") }
    }

}
