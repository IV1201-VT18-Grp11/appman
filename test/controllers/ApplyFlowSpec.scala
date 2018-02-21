package controllers

import org.scalatestplus.play.{FirefoxFactory, OneBrowserPerTest, PlaySpec}
import play.api.test.Injecting
import utils.DbOneServerPerTest

class ApplyFlowSpec
    extends PlaySpec
    with DbOneServerPerTest
    with OneBrowserPerTest
    with FirefoxFactory
    with Injecting {
  "applying to a job" should {
    "be possible" in {
      go to s"http://localhost:$port/jobs/1/"
      click on id(s"apply")

      find(tagName("h1")).value.text must include("Please sign in")
      textField(name("username")).value = "donald_duck"
      pwdField(name("password")).value = "123456"
      click on find(id("login")).value

      find(tagName("h1")).value.text must include("Apply to be a")
      click on find(id("apply")).value

      find(id("description_field")).value.text must include(
        "This field is required"
      )
      textArea(name("description")).value =
        "Well, I probably wouldn't, to be honest..."
      numberField(name("competences[1].experienceYears")).value = "3.2"
      click on dateField(name("availabilities[0].from"))
      pressKeys("2015-02-03")
      click on dateField(name("availabilities[0].to"))
      pressKeys("2015-02-01")
      click on find(id("apply")).value

      find(tagName("body")).value.text must include(
        "The availability period must not end before it begins"
      )
      click on dateField(name("availabilities[0].to"))
      pressKeys("2015-02-05")
      click on find(id("apply")).value

      find(tagName("body")).value.text must (
        include("Well, I probably wouldn't")
          and include("2015-02-03")
          and include("2015-02-05")
          and include("Cooking (3.2 years)")
      )

      Thread.sleep(5000)
    }
  }
}
