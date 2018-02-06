package controllers

import models.UserManager
import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerTest, PlaySpec}
import play.api.test.Injecting
import utils.DbOneServerPerTest


  class RegisterFlowSpec extends PlaySpec with DbOneServerPerTest with OneBrowserPerTest with FirefoxFactory with Injecting {
    "register should work" in {
      go to (s"http://localhost:$port/")
      click on find(id("nav-register")).value

      textField(name("username")).value = "john_doe"
      pwdField(name("password")).value = "123456"
      pwdField(name("confirmPassword")).value = "123456"
      textField(name("firstname")).value = "John"
      textField(name("surname")).value = "Doe"
      textField(name("email")).value = "john_doe@kth.se"
      click on find(id("register")).value
      find(id("message")).value.text must include("You have been registered and logged in")
      find(id("nav-register")) mustBe empty

      click on find(id("nav-logout")).value
      click on find(id("nav-register")).value

      textField(name("username")).value = "john_doe"
      pwdField(name("password")).value = "abc123"
      pwdField(name("confirmPassword")).value = "abc123"
      textField(name("firstname")).value = "John"
      textField(name("surname")).value = "Doe"
      textField(name("email")).value = "john_doe@gmail.com"
      click on find(id("register")).value
      find(tagName("body")).value.text must include("The username is already taken")

      textField(name("username")).value = "jane_doe"
      pwdField(name("password")).value = "abc123"
      pwdField(name("confirmPassword")).value = "abc123"
      textField(name("firstname")).value = "Jane"
      textField(name("surname")).value = "Doe"
      textField(name("email")).value = "john_doe@kth.com"
      click on find(id("register")).value
      find(tagName("body")).value.text must include("The email is already taken")

      textField(name("username")).value = "mickey_mouse"
      pwdField(name("password")).value = "abc123"
      pwdField(name("confirmPassword")).value = "abc111"
      textField(name("firstname")).value = "Mickey"
      textField(name("surname")).value = "Mouse"
      textField(name("email")).value = "mickey_mouse@gmail.com"
      click on find(id("register")).value
      find(id("message")).value.text must include("The passwords you have entered do not match")
    }

}
