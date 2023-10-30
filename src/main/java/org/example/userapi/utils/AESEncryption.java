package org.example.userapi.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 *
 */
public class AESEncryption {

    private static final String DEFAULT_SECRET_KEY = "H5lBbI0V2oN+0dJGXm62Ig==";// TODO This should be securely stored in production - this should not be in the code!!


    private static String getSecretKey() {
        String secretKey = System.getenv("SECRET_KEY");
        return (secretKey != null && !secretKey.isEmpty()) ? secretKey : DEFAULT_SECRET_KEY;
    }

    /**
     * Encrypts the given data using AES (Advanced Encryption Standard) algorithm with a secret key.
     * Converts the data to bytes, encrypts it, and encodes the encrypted bytes to a Base64 string.
     *
     * @param data The data to be encrypted as a string.
     * @return String The Base64 encoded representation of the encrypted data.
     * @throws Exception Thrown if any encryption-related error occurs.
     */
    public static String encrypt(String data) throws Exception {

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be empty");
        }
        // Create a secret key using the specified secret key bytes and AES encryption algorithm
        SecretKey secretKey = new SecretKeySpec(getSecretKey().getBytes(), "AES");

        // Initialize the cipher in encryption mode with the secret key
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Encrypt the data and obtain the encrypted bytes
        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Encode the encrypted bytes to a Base64 string and return the result
        // takes the byte array encryptedData (which contains the encrypted binary data)
        // and encodes it into a Base64-encoded string. This string representation can be
        // easily handled as a regular text string in various contexts, making it suitable
        // for transmission over networks, storage in databases etc
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Decrypts the given Base64-encoded encrypted data using AES (Advanced Encryption Standard) algorithm
     * with a secret key. Decodes the Base64 string, decrypts it, and returns the original data as a string.
     *
     * @param encryptedData The Base64-encoded encrypted data to be decrypted.
     * @return String The decrypted original data as a string.
     * @throws Exception Thrown if any decryption-related error occurs.
     */
    public static String decrypt(String encryptedData) throws Exception {
        // Create a secret key using the specified secret key bytes and AES encryption algorithm
        SecretKey secretKey = new SecretKeySpec(getSecretKey().getBytes(), "AES");

        // Initialize the cipher in decryption mode with the secret key
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decode the Base64 string to obtain the encrypted bytes and decrypt them
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        // Convert the decrypted bytes back to the original data as a string and return the result
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
