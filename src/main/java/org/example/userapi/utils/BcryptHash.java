package org.example.userapi.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.util.AbstractMap;

/**
 * Utility class for hashing and checking passwords using BCrypt hashing algorithm.
 */
public class BcryptHash {

    /**
     * Hashes the input password using BCrypt with a randomly generated salt.
     *
     * @param password The password to be hashed.
     * @return String The hashed password.
     */
    public static AbstractMap.SimpleEntry<String, String> hashPassword(String password) {
        // Generate a random salt
        String salt = BCrypt.gensalt();

        // Hash the password with the generated salt using BCrypt
        String hashedPassword = BCrypt.hashpw(password, salt);
        return new AbstractMap.SimpleEntry<>(hashedPassword, salt);
    }


    /**
     * Checks if the entered password matches the stored hashed password using BCrypt.
     *
     * @param password       The entered password.
     * @param hashedPassword The stored hashed password.
     * @param storedSalt     The stored salt associated with the hashed password.
     * @return true if the entered password matches the stored hashed password, false otherwise.
     */
    public static boolean checkPassword(String password, String hashedPassword, String storedSalt) {
        // Hash the entered password with the stored salt
        String hashedEnteredPassword = BCrypt.hashpw(password, storedSalt);
        // Check if the entered password hashed with the stored salt matches the stored hashed password
        return hashedEnteredPassword.equals(hashedPassword);
    }


    /**
     * Main method for testing hashing and password checking functionality.
     */
    public static void main(String[] args) {
        String password = "mySecurePassword123";

        // Hash the password
        AbstractMap.SimpleEntry<String, String> hashedPassword = hashPassword(password);
        System.out.println("Hashed Password: " + hashedPassword);

        // Check if a password matches the stored hash
        String enteredPassword = "mySecurePassword123";
        if (checkPassword(enteredPassword, hashedPassword.getKey(), hashedPassword.getValue())) {
            System.out.println("Password Matched!");
        } else {
            System.out.println("Incorrect Password!");
        }
    }
}
