package org.example.userapi.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESKeyGenerator {

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
