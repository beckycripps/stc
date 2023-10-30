package org.example.userapi.utils;

import org.junit.jupiter.api.Test;

import static org.example.userapi.utils.AESEncryption.decrypt;
import static org.example.userapi.utils.AESEncryption.encrypt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AESEncryptionTest {

    @Test
    void testEncryptionAndDecryption() throws Exception {
        String originalData = "Sensitive Information";

        // Encrypt the original data
        String encryptedData = encrypt(originalData);
        // Decrypt the encrypted data back to the original data
        String decryptedData = decrypt(encryptedData);

        // Assert that the decrypted data matches the original data
        assertEquals(originalData, decryptedData);
    }

    @Test
    void testEncryptionWithEmptyData() {
        // Assert that encrypting empty data throws an exception
        assertThrows(Exception.class, () -> encrypt(""));
    }

    @Test
    void testDecryptionWithInvalidData() {
        // Assert that decrypting invalid data throws an exception
        assertThrows(Exception.class, () -> decrypt("InvalidBase64EncodedData"));
    }
}
