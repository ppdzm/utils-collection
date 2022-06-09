package io.github.ppdzm.utils.universal.encryption

import org.scalatest.FunSuite

/**
 * Created by Stuart Alex on 2017/3/29.
 */
class EncryptionTest extends FunSuite {

    test("md5") {
        val message = "哈喽, Scala."
        val md5 = PasswordBasedEncryptor.md5Digest(message)
        println(s"md5 is $md5")
    }

    test("string-encrypt") {
        val message = "哈喽, Scala."
        val encryptedMessage = PasswordBasedEncryptor.encrypt(message)
        println(s"message after encrypt: $encryptedMessage")
        val decryptedMessage = PasswordBasedEncryptor.decrypt(encryptedMessage)
        println(s"message after decrypt: $decryptedMessage")
    }

    test("rsa-encrypt") {
        val keyMap = RSA.generateKeyPair()
        val privateKey = RSA.getPrivateKey(keyMap)
        val publicKey = RSA.getPublicKey(keyMap)
        println(s"private key is $privateKey")
        println(s"public key is $publicKey")
        val message = "哈喽, Scala.".getBytes("utf-8")
        val signedMessage = RSA.sign(message, privateKey)
        println(s"signed message is $signedMessage")
        val verified = RSA.verify(message, publicKey, signedMessage)
        println(s"verify result is $verified")
        val encryptedMessageByPrivateKey = RSA.encryptByPrivateKey(message, privateKey)
        val decryptedMessageByPublicKey = RSA.decryptByPublicKey(encryptedMessageByPrivateKey, publicKey)
        println(s"decrypted message by public key is ${new String(decryptedMessageByPublicKey, "utf-8")}")
        val encryptedMessageByPublicKey = RSA.encryptByPublicKey(message, publicKey)
        val decryptedMessageByPrivateKey = RSA.decryptByPrivateKey(encryptedMessageByPublicKey, privateKey)
        println(s"decrypted message by private key is ${new String(decryptedMessageByPrivateKey, "utf-8")}")
    }

}