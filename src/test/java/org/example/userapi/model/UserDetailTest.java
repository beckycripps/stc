package org.example.userapi.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.example.userapi.utils.AESEncryption.encrypt;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailTest {

    @Test
    public void testToDTO() throws Exception {
        // Arrange
        UserDetail userDetail = new UserDetail();//should be the data that is stored in the database

        //create the encrypted and hashed userDetails that should have been stored in the database
        userDetail.setEmail("test@example.com");
        userDetail.setPassword("hashedPassword");
        userDetail.setName(encrypt("Name"));
        userDetail.setAddress(encrypt("Address"));
        List<Donation> donations = new ArrayList<>();
        Donation donation = new Donation();
        donation.setAmount(encrypt("100"));
        donation.setDonationDate(LocalDateTime.now());
        donations.add(donation);
        // Act
        UserResponseDTO userResponseDTO = userDetail.toDTO(donations);

        // Assert
        assertNotNull(userResponseDTO);
        assertEquals("test@example.com", userResponseDTO.getEmail());
        assertEquals("Name", userResponseDTO.getName());
        assertEquals("Address", userResponseDTO.getAddress());
        assertEquals(1, userResponseDTO.getDonationsDTO().size());
        assertEquals("100", userResponseDTO.getDonationsDTO().get(0).getAmount());
        assertNotNull(userResponseDTO.getDonationsDTO().get(0).getDonationDate());
    }
}
