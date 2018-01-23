package controllers

import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerTest, PlaySpec}
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.test.Injecting


  class RegisterFlowSpec extends PlaySpec with GuiceOneServerPerTest with OneBrowserPerTest with FirefoxFactory with Injecting {
    "register should work" in {
      go to (s"http://localhost:$port/")
      click on find(id("nav-register")).value
      textField(name("username")).value = "donald_duck"
      pwdField(name("password")).value = "123456"
      click on find(id("register")).value
      eventually { find(id("messages")).value.text must include("You have been registered") }
    }

}
