package org.example.userapi.service;


import org.example.userapi.model.Donation;
import org.example.userapi.model.UserDetail;
import org.example.userapi.model.UserDetailDTO;
import org.example.userapi.model.UserResponseDTO;
import org.example.userapi.repository.DonationRepository;
import org.example.userapi.repository.UserRepository;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;

import static org.example.userapi.utils.AESEncryption.encrypt;
import static org.example.userapi.utils.BcryptHash.checkPassword;
import static org.example.userapi.utils.BcryptHash.hashPassword;

/**
 *
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    public UserService(UserRepository userRepository, DonationRepository donationRepository) {
        this.userRepository = userRepository;
        this.donationRepository = donationRepository;
    }

    /**
     * Validates and sanitizes user input to prevent security vulnerabilities.
     *
     * @param user The UserDetailDTO object containing user information to be validated and sanitized.
     * @return ValidationResult A ValidationResult object indicating the success of validation and sanitization.
     * Contains a boolean flag indicating validation success.
     */
    public ValidationResult validateAndSanitizeUser(@Valid @NotNull UserDetailDTO user) {
        ValidationResult validationResult = new ValidationResult();

        // Validate user fields using javax.validation annotations
        // (Validation logic defined by annotations like @NotNull, @Size, etc.)
        // Sanitize user input to prevent XSS attacks
        String sanitizedName = Encode.forHtml(user.getName());
        String sanitizedEmail = Encode.forHtml(user.getEmail());
        String sanitizedPassword = Encode.forHtml(user.getPassword());
        String sanitizedAddress = Encode.forHtml(user.getAddress());

        // Set sanitized values back to the user object
        user.setName(sanitizedName);
        user.setEmail(sanitizedEmail);
        user.setPassword(sanitizedPassword);
        user.setAddress(sanitizedAddress);


        // Set validation result based on validation and sanitization logic
        validationResult.setValid(true);

        return validationResult;
    }

    /**
     * Creates a new user with the provided details or throws an exception if a user with the same email already exists.
     *
     * @param userDetailDTO The DTO containing user details.
     * @throws Exception If a user with the provided email already exists.
     */
    public void createUser(UserDetailDTO userDetailDTO) throws Exception {

        UserDetail savedUserDetail = userRepository.findByEmail(userDetailDTO.getEmail());
        if (savedUserDetail == null) {
            AbstractMap.SimpleEntry<String, String> hashedPassword = hashPassword(userDetailDTO.getPassword());
            UserDetail userDetail = new UserDetail();
            userDetail.setPassword(hashedPassword.getKey());
            userDetail.setPasswordSalt(hashedPassword.getValue());
            //encrypt the user information before saving
            userDetail.setEmail(userDetailDTO.getEmail());
            userDetail.setName(encrypt(userDetailDTO.getName()));
            userDetail.setAddress(encrypt(userDetailDTO.getAddress()));

            UserDetail result = userRepository.save(userDetail);

            if (userDetailDTO.getDonationAmount() != null) {
                Donation donation = new Donation();
                donation.setAmount(encrypt(userDetailDTO.getDonationAmount().toString()));
                donation.setDonationDate(LocalDateTime.now());
                donation.setUserDetail(result);
                donationRepository.save(donation);
            }
        } else {
            // Throw an Exception if the password does not match
            throw new Exception("User already exists");
        }
    }


    /**
     * Updates the details of an existing user based on the provided UserDetailDTO
     * after verifying the provided password.
     * Encrypts and saves the updated name and address to the saved user details.
     * If a donation amount is provided, creates a new Donation object, encrypts the amount,
     * sets the donation date, and associates it with the existing user.
     *
     * @param userDetailDTO The UserDetailDTO object containing updated user information.
     * @throws Exception Thrown in case of unexpected errors during encryption or database operations.
     */
    public void updateUserDetails(UserDetailDTO userDetailDTO) throws Exception {

        UserDetail savedUserDetail = userRepository.findByEmail(userDetailDTO.getEmail());
        if (checkPassword(userDetailDTO.getPassword(), savedUserDetail.getPassword(), savedUserDetail.getPasswordSalt())) {
            // Encrypt and update the user's name and address
            savedUserDetail.setName(encrypt(userDetailDTO.getName()));
            savedUserDetail.setAddress(encrypt(userDetailDTO.getAddress()));
            // Save the updated user details to the repository
            userRepository.save(savedUserDetail);

            // Check if a donation amount is provided in the userDetailDTO
            if (userDetailDTO.getDonationAmount() != null) {
                // If donation amount is provided, create a new Donation object
                Donation donation = new Donation();
                // Encrypt and set the donation amount
                donation.setAmount(encrypt(userDetailDTO.getDonationAmount().toString()));
                // Set the donation date to the current date and time
                donation.setDonationDate(LocalDateTime.now());
                // Associate the donation with the existing user
                donation.setUserDetail(savedUserDetail);
                // Save the new donation to the donation repository
                donationRepository.save(donation);
            }
        } else {
            throw new Exception("Invalid Password");
        }
    }


    /**
     * Retrieves user information by the provided email address.
     * Creates a UserResponseDTO object containing user details and associated donations.
     * If the user exists, retrieves associated donations and converts the UserDetail object
     * to a UserResponseDTO. If any exception occurs during this process, logs the error.
     *
     * @param email The email address of the user to be retrieved.
     * @return UserResponseDTO A Data Transfer Object representing user details and donations.
     * Returns null if no user is found for the provided email.
     */
    public UserResponseDTO getUserByEmail(String email, String password) {
        // Retrieve user details from the repository based on the provided email
        UserDetail userDetail = userRepository.findByEmail(email);

        // Check if the user with the given email exists and the password matches
        if (userDetail != null && checkPassword(password, userDetail.getPassword(), userDetail.getPasswordSalt())) {
            try {
                // Retrieve associated donations from the donation repository
                List<Donation> donations = donationRepository.findByUserDetail(userDetail);

                // Convert UserDetail object to UserResponseDTO, including associated donations
                if (!donations.isEmpty()) {
                    return userDetail.toDTO(donations);
                }
            } catch (Exception e) {
                // Log any exceptions that occur during the conversion process
                logger.error("Exception occurred: ", e);
            }
        }
        // If no user is found for the provided email or the password is wrong, return null
        return null;
    }

    /**
     * Deletes a user based on the provided email address and password.
     * Verifies the user's password before deletion. If the user exists and the provided password matches,
     * deletes the user from the repository and returns true. If no user is found or the password does not match,
     * returns false indicating unsuccessful deletion.
     *
     * @param email    The email address of the user to be deleted.
     * @param password The password provided for authentication.
     * @return boolean True if the user is successfully deleted, false otherwise.
     */
    public boolean deleteUser(String email, String password) {
        UserDetail userDetail = userRepository.findByEmail(email);
        if (userDetail != null && checkPassword(password, userDetail.getPassword(), userDetail.getPasswordSalt())) {
            userRepository.delete(userDetail);
            return true;
        }
        return false;
    }

}
