package models

import com.google.inject.ImplementedBy
import org.apache.commons.codec.binary.Hex
import org.bouncycastle.crypto.PasswordConverter
import org.bouncycastle.crypto.digests.Blake2bDigest
import org.bouncycastle.crypto.generators.SCrypt
import org.bouncycastle.crypto.prng.DigestRandomGenerator

@ImplementedBy(classOf[ScryptPasswordHasher])
trait PasswordHasher {
  def hash(plaintext: String): String
  def compare(hashed: String, plaintext: String): Boolean
}

class ScryptPasswordHasher extends PasswordHasher {
  private val pwConverter = PasswordConverter.UTF8
  private val rng = new DigestRandomGenerator(new Blake2bDigest(512))

  private def newSalt: Array[Byte] = {
    val buf = Array.ofDim[Byte](32)
    rng.nextBytes(buf)
    buf
  }

  private def hashWithSalt(plaintext: String, salt: Array[Byte]): String = {
    val bytes = pwConverter.convert(plaintext.toCharArray())
    val cipherText = SCrypt.generate(bytes, salt, 8, 8, 8, 64)
    Hex.encodeHexString(cipherText)
  }

  override def hash(plaintext: String): String = {
    val salt = newSalt
    val cipher = hashWithSalt(plaintext, salt)
    s"${Hex.encodeHexString(salt)}&$cipher"
  }

  override def compare(hashed: String, plaintext: String): Boolean = {
    val Array(saltStr, cipherText) = hashed.split("&")
    val salt = Hex.decodeHex(saltStr)
    hashWithSalt(plaintext, salt) == cipherText
  }
}
