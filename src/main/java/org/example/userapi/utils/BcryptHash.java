package org.example.userapi.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptHash {

    public static String hashPassword(String password) {
        // Hash a password with a randomly generated salt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        // Check if the entered password matches the stored hashed password
        return BCrypt.checkpw(password, hashedPassword);
    }

    public static void main(String[] args) {
        String password = "mySecurePassword123";

        // Hash the password
        String hashedPassword = hashPassword(password);
        System.out.println("Hashed Password: " + hashedPassword);

        // Check if a password matches the stored hash
        String enteredPassword = "mySecurePassword123";
        if (checkPassword(enteredPassword, hashedPassword)) {
            System.out.println("Password Matched!");
        } else {
            System.out.println("Incorrect Password!");
        }
    }
}
