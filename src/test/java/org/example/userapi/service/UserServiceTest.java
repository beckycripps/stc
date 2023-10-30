package org.example.userapi.service;

import org.example.userapi.model.Donation;
import org.example.userapi.model.UserDetail;
import org.example.userapi.model.UserDetailDTO;
import org.example.userapi.model.UserResponseDTO;
import org.example.userapi.repository.DonationRepository;
import org.example.userapi.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.example.userapi.utils.AESEncryption.encrypt;
import static org.example.userapi.utils.BcryptHash.hashPassword;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private UserService userService;

    private final UserDetailDTO userDetailDTO = new UserDetailDTO();
    private final UserDetail savedUserDetail = new UserDetail();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userDetailDTO.setEmail("test@example.com");
        userDetailDTO.setPassword("password");
        userDetailDTO.setName("Test Name");
        userDetailDTO.setAddress("test address");
        AbstractMap.SimpleEntry<String, String> hashedPassword = hashPassword("password");
        savedUserDetail.setPassword(hashedPassword.getKey()); // Simulate hashed password
        savedUserDetail.setPasswordSalt(hashedPassword.getValue());
        savedUserDetail.setEmail("test@example.com");
        savedUserDetail.setName(encrypt("Test Name"));
        savedUserDetail.setAddress(encrypt("test address"));

    }

    @Test
    void testValidateAndSanitizeUser() {
        // Arrange
        UserDetailDTO userDetailDTOXSS = new UserDetailDTO();
        userDetailDTOXSS.setName("<script>alert('XSS')</script>");
        userDetailDTOXSS.setEmail("test@example.com");
        userDetailDTOXSS.setPassword("password");
        userDetailDTOXSS.setAddress("<script>alert('XSS')</script>");

        // Act
        ValidationResult result = userService.validateAndSanitizeUser(userDetailDTOXSS);

        // Assert
        Assertions.assertTrue(result.isValid());
        Assertions.assertEquals("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;", userDetailDTOXSS.getName());
        Assertions.assertEquals("test@example.com", userDetailDTOXSS.getEmail());
        Assertions.assertEquals("password", userDetailDTOXSS.getPassword());
        Assertions.assertEquals("&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;", userDetailDTOXSS.getAddress());
    }

    @Test
    void testCreateUserWhenUserDoesNotExist() throws Exception {
        // Arrange
        // Mock repository
        when(userRepository.findByEmail(userDetailDTO.getEmail())).thenReturn(null);
        // Act
        userService.createUser(userDetailDTO);
        // Assert
        verify(userRepository, times(1)).save(any(UserDetail.class));

    }

    @Test
    void testCreateUserThrowsExceptionWhenUserExists() {

        UserDetail existingUser = new UserDetail();
        // Mock repository behavior
        when(userRepository.findByEmail(userDetailDTO.getEmail())).thenReturn(existingUser);

        // Act & Assert
        try {
            userService.createUser(userDetailDTO);
        } catch (Exception e) {
            // Verify that the userRepository.findByEmail method is called once
            verify(userRepository, times(1)).findByEmail(userDetailDTO.getEmail());
            // Verify that the exception is thrown
            assert (e.getMessage().contains("User already exists"));
            return;
        }

        // Fail the test if no exception is thrown
        Assertions.fail("Expected exception was not thrown");
    }


    @Test
    void testUpdateUserWhenUserExistsAndPasswordDoesNotMatch() {

        userDetailDTO.setPassword("wrong_password");

        // Mock repository
        when(userRepository.findByEmail(userDetailDTO.getEmail())).thenReturn(savedUserDetail);

        // Act and Assert
        Assert.assertThrows(Exception.class, () -> userService.updateUserDetails(userDetailDTO));

        // Verify interactions
        verify(userRepository, never()).save(any(UserDetail.class));
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void testGetUserByEmailWhenUserExists() {

        List<Donation> donations = new ArrayList<>();
        donations.add(new Donation());

        // Mock repository
        when(userRepository.findByEmail(userDetailDTO.getEmail())).thenReturn(savedUserDetail);
        when(donationRepository.findByUserDetail(savedUserDetail)).thenReturn(donations);

        // Act
        UserResponseDTO result = userService.getUserByEmail(userDetailDTO.getEmail(), userDetailDTO.getPassword());

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDetailDTO.getEmail(), result.getEmail());
        Assertions.assertFalse(result.getDonationsDTO().isEmpty());
    }

    @Test
    void testGetUserByEmailWhenPasswordWrong() {

        // Mock repository
        when(userRepository.findByEmail(userDetailDTO.getEmail())).thenReturn(savedUserDetail);

        // Act
        UserResponseDTO result = userService.getUserByEmail(userDetailDTO.getEmail(), userDetailDTO.getPassword());

        // Assert
        Assertions.assertNull(result);
    }

    @Test
    void testGetUserByEmailWhenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";


        // Mock repository
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act
        UserResponseDTO result = userService.getUserByEmail(email, "fake password");

        // Assert
        Assertions.assertNull(result);
    }

    @Test
    void testDeleteUserSuccess() {
        String password = "password";

        // Mocking the behavior of UserRepository.findByEmail()
        when(userRepository.findByEmail(userDetailDTO.getEmail())).thenReturn(savedUserDetail);


        // Call the deleteUser method
        boolean result = userService.deleteUser(userDetailDTO.getEmail(), password);

        // Verify that delete method is called and the result is true
        verify(userRepository, times(1)).delete(savedUserDetail);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUserWrongPasswordFailure() {
        String email = "test@example.com";
        String password = "password123";

        // Mocking the behavior of UserRepository.findByEmail()
        when(userRepository.findByEmail(email)).thenReturn(savedUserDetail);

        // Call the deleteUser method
        boolean result = userService.deleteUser(email, password);

        // Verify that delete method is not called and the result is false
        verify(userRepository, never()).delete(savedUserDetail);
        Assertions.assertFalse(result);
    }

    @Test
    void testDeleteUserNotFound() {
        String email = "nonexistent@example.com";
        String password = "password123";

        // Mocking the behavior of UserRepository.findByEmail() for a non-existent user
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Call the deleteUser method
        boolean result = userService.deleteUser(email, password);

        // Verify that delete method is not called and the result is false
        verify(userRepository, never()).delete(any());
        Assertions.assertFalse(result);
    }
}
