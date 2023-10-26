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
import java.util.List;

import static org.example.userapi.utils.AESEncryption.encrypt;
import static org.example.userapi.utils.BcryptHash.checkPassword;
import static org.example.userapi.utils.BcryptHash.hashPassword;

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

    public ValidationResult validateAndSanitizeUser(@Valid @NotNull UserDetailDTO user) {
        ValidationResult validationResult = new ValidationResult();

        // Validate user fields using javax.validation annotations

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


    public void createUser(UserDetailDTO userDetailDTO) throws Exception {
        // hash the password before saving
        //if the user already exists - update details or add the donation
        UserDetail savedUserDetail = userRepository.findByEmail(userDetailDTO.getEmail());
        if (savedUserDetail == null) {
            createUserDetails(userDetailDTO);
        } else {
            if (checkPassword(userDetailDTO.getPassword(), savedUserDetail.getPassword())) {
                updateUserDetails(userDetailDTO, savedUserDetail);
            } else {
                System.out.println("fuckit");
            }
        }

    }

    private void updateUserDetails(UserDetailDTO userDetailDTO, UserDetail savedUserDetail) throws Exception {
        savedUserDetail.setName(encrypt(userDetailDTO.getName()));
        savedUserDetail.setAddress(encrypt(userDetailDTO.getAddress()));
        userRepository.save(savedUserDetail);
        if (userDetailDTO.getDonationAmount() != null) {
            Donation donation = new Donation();
            donation.setAmount(encrypt(userDetailDTO.getDonationAmount().toString()));
            donation.setDonationDate(LocalDateTime.now());
            donation.setUserDetail(savedUserDetail);
            // Add the new donation to the existing user's donations list
            donationRepository.save(donation);
        }
    }

    private void createUserDetails(UserDetailDTO userDetailDTO) throws Exception {
        UserDetail userDetail = new UserDetail();
        userDetail.setPassword(hashPassword(userDetailDTO.getPassword()));
        //encrypt the user information before saving
        userDetail.setEmail(userDetailDTO.getEmail());
        userDetail.setName(encrypt(userDetailDTO.getName()));
        userDetail.setAddress(encrypt(userDetailDTO.getAddress()));

        UserDetail savedUserDetail = userRepository.save(userDetail);

        if (userDetailDTO.getDonationAmount() != null) {
            Donation donation = new Donation();
            donation.setAmount(encrypt(userDetailDTO.getDonationAmount().toString()));
            donation.setDonationDate(LocalDateTime.now());
            donation.setUserDetail(savedUserDetail);
            donationRepository.save(donation);
        }
    }


    public UserResponseDTO getUserByEmail(String email) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        UserDetail userDetail = userRepository.findByEmail(email);
        if (userDetail != null) {
            try {
                List<Donation> donations = donationRepository.findByUserDetail(userDetail);
                userResponseDTO = userDetail.toDTO(donations);
            } catch (Exception e) {
                logger.error("Exception occurred: ", e);
            }
        } else {
            userResponseDTO = null;
        }
        System.out.println("getUserByEmail - userDetailsDTO" + userResponseDTO);
        return userResponseDTO;
    }

    public boolean deleteUser(String email, String password) {
        UserDetail userDetail = userRepository.findByEmail(email);
        if (userDetail != null && checkPassword(password, userDetail.getPassword())) {
            userRepository.delete(userDetail);
            return true;
        }
        return false;
    }

}
