package io.github.ppdzm.utils.universal.encryption

import java.io.ByteArrayOutputStream
import java.security._
import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.util
import java.util.Base64

import io.github.ppdzm.utils.universal.feature.LoanPattern
import javax.crypto.Cipher

/**
 * Created by Stuart Alex on 2017/3/29.
 */
object RSA {
    private val KEY_ALGORITHM = "RSA"
    private val SIGNATURE_ALGORITHM = "MD5withRSA"
    private val PUBLIC_KEY = "RSAPublicKey"
    private val PRIVATE_KEY = "RSAPrivateKey"
    private val MAX_ENCRYPT_BLOCK = 117
    private val MAX_DECRYPT_BLOCK = 128
    private val base64Decoder = Base64.getDecoder
    private val base64Encoder = Base64.getEncoder

    /**
     * 生成公钥、私钥
     *
     * @return
     */
    def generateKeyPair(): util.HashMap[String, Any] = {
        val keyPairGenerator = KeyPairGenerator.getInstance(this.KEY_ALGORITHM)
        keyPairGenerator.initialize(1024)
        val keyPair = keyPairGenerator.generateKeyPair
        val publicKey = keyPair.getPublic.asInstanceOf[RSAPublicKey]
        val privateKey = keyPair.getPrivate.asInstanceOf[RSAPrivateKey]
        val keyMap = new util.HashMap[String, Any](2)
        keyMap.put(this.PUBLIC_KEY, publicKey)
        keyMap.put(this.PRIVATE_KEY, privateKey)
        keyMap
    }

    /**
     * 使用私钥对数据进行签名
     *
     * @param data             数据
     * @param privateKeyString 私钥
     * @return
     */
    def sign(data: Array[Byte], privateKeyString: String): String = {
        val keyBytes = Base64.getDecoder.decode(privateKeyString)
        val pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM)
        val privateKey = keyFactory.generatePrivate(pkcs8KeySpec)
        val signature = Signature.getInstance(this.SIGNATURE_ALGORITHM)
        signature.initSign(privateKey)
        signature.update(data)
        new String(base64Encoder.encode(signature.sign))
    }

    /**
     * 使用公钥对数据进行校验
     *
     * @param data            数据
     * @param publicKeyString 公钥
     * @param sign            私钥签名后的数据
     * @return
     */
    def verify(data: Array[Byte], publicKeyString: String, sign: String): Boolean = {
        val keyBytes = base64Decoder.decode(publicKeyString)
        val keySpec = new X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM)
        val publicKey = keyFactory.generatePublic(keySpec)
        val signature = Signature.getInstance(this.SIGNATURE_ALGORITHM)
        signature.initVerify(publicKey)
        signature.update(data)
        signature.verify(base64Decoder.decode(sign))
    }

    /**
     * 使用私钥进行解密
     *
     * @param encryptedData    要解密的数据
     * @param privateKeyString 私钥
     * @return
     */
    def decryptByPrivateKey(encryptedData: Array[Byte], privateKeyString: String): Array[Byte] = {
        val keyBytes = base64Decoder.decode(privateKeyString)
        val pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM)
        val privateKey = keyFactory.generatePrivate(pkcs8KeySpec)
        val cipher = Cipher.getInstance(keyFactory.getAlgorithm)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val inputLength = encryptedData.length
        val batchCount = inputLength / this.MAX_DECRYPT_BLOCK
        LoanPattern.using(new ByteArrayOutputStream)(out => {
            (0 until batchCount).foreach(i => {
                val cache = cipher.doFinal(encryptedData, i * this.MAX_DECRYPT_BLOCK, this.MAX_DECRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            })
            if (inputLength > batchCount * this.MAX_DECRYPT_BLOCK) {
                val cache = cipher.doFinal(encryptedData, batchCount * this.MAX_DECRYPT_BLOCK, inputLength - batchCount * this.MAX_DECRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            }
            out.toByteArray
        })
    }

    /**
     * 使用公钥进行解密
     *
     * @param encryptedData   要解密的数据
     * @param publicKeyString 公钥
     * @return
     */
    def decryptByPublicKey(encryptedData: Array[Byte], publicKeyString: String): Array[Byte] = {
        val keyBytes = base64Decoder.decode(publicKeyString)
        val x509KeySpec = new X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM)
        val publicKey = keyFactory.generatePublic(x509KeySpec)
        val cipher = Cipher.getInstance(keyFactory.getAlgorithm)
        cipher.init(Cipher.DECRYPT_MODE, publicKey)
        val inputLength = encryptedData.length
        val batchCount = inputLength / this.MAX_DECRYPT_BLOCK
        LoanPattern.using(new ByteArrayOutputStream)(out => {
            (0 until batchCount).foreach(i => {
                val cache = cipher.doFinal(encryptedData, i * this.MAX_DECRYPT_BLOCK, this.MAX_DECRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            })
            if (inputLength > batchCount * this.MAX_DECRYPT_BLOCK) {
                val cache = cipher.doFinal(encryptedData, batchCount * this.MAX_DECRYPT_BLOCK, inputLength - batchCount * this.MAX_DECRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            }
            out.toByteArray
        })
    }

    /**
     * 使用私钥进行加密
     *
     * @param data             要加密的数据
     * @param privateKeyString 私钥
     * @return
     */
    def encryptByPrivateKey(data: Array[Byte], privateKeyString: String): Array[Byte] = {
        val keyBytes = base64Decoder.decode(privateKeyString)
        val pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM)
        val privateKey = keyFactory.generatePrivate(pkcs8KeySpec)
        val cipher = Cipher.getInstance(keyFactory.getAlgorithm)
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)
        val inputLength = data.length
        val batchCount = inputLength / this.MAX_ENCRYPT_BLOCK
        LoanPattern.using(new ByteArrayOutputStream)(out => {
            (0 until batchCount).foreach(i => {
                val cache = cipher.doFinal(data, i * this.MAX_ENCRYPT_BLOCK, this.MAX_ENCRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            })
            if (inputLength > batchCount * this.MAX_ENCRYPT_BLOCK) {
                val cache = cipher.doFinal(data, batchCount * this.MAX_ENCRYPT_BLOCK, inputLength - batchCount * this.MAX_ENCRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            }
            out.toByteArray
        })
    }

    /**
     * 使用公钥进行加密
     *
     * @param data            要加密的数据
     * @param publicKeyString 公钥
     * @return
     */
    def encryptByPublicKey(data: Array[Byte], publicKeyString: String): Array[Byte] = {
        val keyBytes = base64Decoder.decode(publicKeyString)
        val x509KeySpec = new X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM)
        val publicKey = keyFactory.generatePublic(x509KeySpec)
        val cipher = Cipher.getInstance(keyFactory.getAlgorithm)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val inputLength = data.length
        val batchCount = inputLength / this.MAX_ENCRYPT_BLOCK
        LoanPattern.using(new ByteArrayOutputStream)(out => {
            (0 until batchCount).foreach(i => {
                val cache = cipher.doFinal(data, i * this.MAX_ENCRYPT_BLOCK, this.MAX_ENCRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            })
            if (inputLength > batchCount * this.MAX_ENCRYPT_BLOCK) {
                val cache = cipher.doFinal(data, batchCount * this.MAX_ENCRYPT_BLOCK, inputLength - batchCount * this.MAX_ENCRYPT_BLOCK)
                out.write(cache, 0, cache.length)
            }
            out.toByteArray
        })
    }

    /**
     * 取出私钥
     *
     * @param keyMap Map形式存储的公钥、私钥
     * @return
     */
    def getPrivateKey(keyMap: util.Map[String, Any]): String = {
        val key = keyMap.get(this.PRIVATE_KEY).asInstanceOf[Key]
        new String(base64Encoder.encode(key.getEncoded))
    }

    /**
     * 取出公钥
     *
     * @param keyMap Map形式存储的公钥、私钥
     * @return
     */
    def getPublicKey(keyMap: util.Map[String, Any]): String = {
        val key: Key = keyMap.get(this.PUBLIC_KEY).asInstanceOf[Key]
        new String(base64Encoder.encode(key.getEncoded))
    }

}