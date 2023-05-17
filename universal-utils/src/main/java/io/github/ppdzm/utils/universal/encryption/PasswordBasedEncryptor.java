package io.github.ppdzm.utils.universal.encryption;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Created by Stuart Alex on 2017/3/29.
 */
public class PasswordBasedEncryptor {
    private static final String DEFAULT_PASSWORD = "https://www.xyz.com";

    /**
     * Encrypts the message using the specified password.
     *
     * @param message  The message to encrypt.
     * @param password The password.
     * @return The encrypted message.
     */
    public static String encrypt(String message, String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword(password);
        encryptor.setConfig(config);
        return encryptor.encrypt(message);
    }

    /**
     * Encrypts the message using the default password.
     *
     * @param message The message to encrypt.
     * @return The encrypted message.
     */
    public static String encrypt(String message) {
        return encrypt(message, DEFAULT_PASSWORD);
    }

    /**
     * Decrypts the encrypted message using the specified password.
     *
     * @param encryptedMessage The encrypted message.
     * @param password         The password.
     * @return The decrypted message.
     */
    public static String decrypt(String encryptedMessage, String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword(password);
        encryptor.setConfig(config);
        return encryptor.decrypt(encryptedMessage);
    }

    /**
     * Decrypts the encrypted message using the default password.
     *
     * @param encryptedMessage The encrypted message.
     * @return The decrypted message.
     */
    public static String decrypt(String encryptedMessage) {
        return decrypt(encryptedMessage, DEFAULT_PASSWORD);
    }

    /**
     * Calculates the MD5 digest of the message.
     *
     * @param message The message to encrypt.
     * @return The MD5 digest of the message.
     */
    public static String md5Digest(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

}

