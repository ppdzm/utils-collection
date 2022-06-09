package io.github.ppdzm.utils.universal.encryption

import java.security.MessageDigest

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object PasswordBasedEncryptor {
    private val defaultPassword = "https://www.xyz.com"

    /**
     * 使用指定的密码加密信息
     *
     * @param message  需要加密的信息
     * @param password 密码
     * @return 加密后的信息
     */
    def encrypt(message: String, password: String = this.defaultPassword): String = {
        val encryptor = new StandardPBEStringEncryptor()
        val config = new EnvironmentStringPBEConfig()
        config.setAlgorithm("PBEWithMD5AndDES")
        config.setPassword(password)
        encryptor.setConfig(config)
        encryptor.encrypt(message)
    }

    /**
     * 使用指定的密码解密加密后的信息
     *
     * @param encryptedMessage 加密后的信息
     * @param password         密码
     * @return 信息
     */
    def decrypt(encryptedMessage: String, password: String = this.defaultPassword): String = {
        val encryptor = new StandardPBEStringEncryptor()
        val config = new EnvironmentStringPBEConfig()
        config.setAlgorithm("PBEWithMD5AndDES")
        config.setPassword(password)
        encryptor.setConfig(config)
        encryptor.decrypt(encryptedMessage)
    }

    /**
     * MD5加密
     *
     * @param message 需要加密的信息
     * @return MD5加密后的信息
     */
    def md5Digest(message: String): String = {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(message.getBytes("utf-8"))
        bytes.map(b => {
            val string = Integer.toHexString(b & 0xFF)
            if (string.length == 1)
                "0" + string.toUpperCase()
            else
                string.toUpperCase()
        }).mkString
    }

}