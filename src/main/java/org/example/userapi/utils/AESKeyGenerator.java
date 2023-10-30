package org.example.userapi.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * A utility class to generate an AESKEY for use in encryption - this would not
 * be required in production. The key would be generated and saved in the environment variables
 */
public class AESKeyGenerator {
    /**
     * Generates a new AES (Advanced Encryption Standard) secret key.
     *
     * @return A new AES SecretKey object.
     * @throws NoSuchAlgorithmException If the specified algorithm (AES) is not available in the provider.
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128, 192, or 256 bits
        return keyGenerator.generateKey();
    }


    public static void main(String[] args) {
        try {
            SecretKey aesKey = generateAESKey();
            System.out.println("Generated AES Key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
