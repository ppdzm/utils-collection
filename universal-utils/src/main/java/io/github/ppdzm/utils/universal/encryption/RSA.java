package io.github.ppdzm.utils.universal.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2017/3/29.
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class RSA {
    private final static String KEY_ALGORITHM = "RSA";
    private final static String SIGNATURE_ALGORITHM = "MD5withRSA";
    private final static String PUBLIC_KEY = "RSAPublicKey";
    private final static String PRIVATE_KEY = "RSAPrivateKey";
    private final static int MAX_ENCRYPT_BLOCK = 117;
    private final static int MAX_DECRYPT_BLOCK = 128;
    private final static Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private final static Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    /**
     * 生成公钥、私钥
     */
    public static Map<String, Object> generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        HashMap<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 使用私钥对数据进行签名
     *
     * @param data             数据
     * @param privateKeyString 私钥
     */
    public static String sign(byte[] data, String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return new String(BASE64_ENCODER.encode(signature.sign()));
    }

    /**
     * 使用公钥对数据进行校验
     *
     * @param data            数据
     * @param publicKeyString 公钥
     * @param sign            私钥签名后的数据
     */
    public static boolean verify(byte[] data, String publicKeyString, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] keyBytes = BASE64_DECODER.decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(BASE64_DECODER.decode(sign));
    }

    /**
     * 使用私钥进行解密
     *
     * @param encryptedData    要解密的数据
     * @param privateKeyString 私钥
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKeyString) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] keyBytes = BASE64_DECODER.decode(privateKeyString);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        ByteArrayOutputStream out = run(encryptedData, keyFactory, privateKey, Cipher.DECRYPT_MODE, MAX_DECRYPT_BLOCK);
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }

    /**
     * 使用公钥进行解密
     *
     * @param encryptedData   要解密的数据
     * @param publicKeyString 公钥
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKeyString) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] keyBytes = BASE64_DECODER.decode(publicKeyString);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        ByteArrayOutputStream out = run(encryptedData, keyFactory, publicKey, Cipher.DECRYPT_MODE, MAX_DECRYPT_BLOCK);
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }

    /**
     * 使用私钥进行加密
     *
     * @param data             要加密的数据
     * @param privateKeyString 私钥
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKeyString) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] keyBytes = BASE64_DECODER.decode(privateKeyString);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        ByteArrayOutputStream out = run(data, keyFactory, privateKey, Cipher.ENCRYPT_MODE, MAX_ENCRYPT_BLOCK);
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }

    /**
     * 使用公钥进行加密
     *
     * @param data            要加密的数据
     * @param publicKeyString 公钥
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] keyBytes = BASE64_DECODER.decode(publicKeyString);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        ByteArrayOutputStream out = run(data, keyFactory, publicKey, Cipher.ENCRYPT_MODE, MAX_ENCRYPT_BLOCK);
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }

    private static ByteArrayOutputStream run(byte[] encryptedData, KeyFactory keyFactory, Key key, int mode, int maxBlock) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(mode, key);
        int inputLength = encryptedData.length;
        int batchCount = inputLength / maxBlock;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < batchCount; i++) {
            byte[] cache = cipher.doFinal(encryptedData, i * maxBlock, maxBlock);
            out.write(cache, 0, cache.length);
        }
        if (inputLength > batchCount * maxBlock) {
            byte[] cache = cipher.doFinal(encryptedData, batchCount * maxBlock, inputLength - batchCount * maxBlock);
            out.write(cache, 0, cache.length);
        }
        return out;
    }

    /**
     * 取出私钥
     *
     * @param keyMap Map形式存储的公钥、私钥
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return new String(BASE64_ENCODER.encode(key.getEncoded()));
    }

    /**
     * 取出公钥
     *
     * @param keyMap Map形式存储的公钥、私钥
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return new String(BASE64_ENCODER.encode(key.getEncoded()));
    }

}
