package org.example.userapi.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncryption {

    private static final String SECRET_KEY = "H5lBbI0V2oN+0dJGXm62Ig=="; // TODO This should be securely stored in production - this should not be in the code!!

    public static String encrypt(String data) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

    public static void main(String[] args) throws Exception {
        String originalData = "Sensitive Information";
        String encryptedData = encrypt(originalData);
        System.out.println("Encrypted Data: " + encryptedData);

        String decryptedData = decrypt(encryptedData);
        System.out.println("Decrypted Data: " + decryptedData);
    }
}
