package models

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

abstract class PasswordHasherSpec(hasher: PasswordHasher) extends PlaySpec {
  "calling hash()" should {
    "not return the original value" in {
      hasher.hash("very secret") mustNot include("very secret")
    }

    "be salted" in {
      hasher.hash("very secret") mustNot be(hasher.hash("very secret"))
    }
  }

  "comparing a hash and a plaintext" when {
    "the hash was derived from the plaintext" should {
      "return true" in {
        hasher.compare(hasher.hash("very secret"), "very secret") mustBe true
      }
    }

    "the hash was not derived from the plaintext" should {
      "return false" in {
        hasher.compare(hasher.hash("very secret"), "very public") mustBe false
      }
    }
  }
}

class ScryptPasswordHasherSpec extends PasswordHasherSpec(new ScryptPasswordHasher())

