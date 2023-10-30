package org.example.userapi.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.userapi.utils.AESEncryption.decrypt;

/**
 *
 */
@Data
@Entity
@Table(name = "user_detail")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    @Column(name = "password_salt")
    private String passwordSalt;
    private String password;

    // Address can be blank, no specific constraints set here
    private String address;

    /**
     * Converts the UserDetail entity along with associated donations to a UserResponseDTO.
     *
     * @param donations List of Donation entities associated with the user.
     * @return UserResponseDTO containing user details and decrypted donation information.
     * @throws Exception If there's an error during decryption.
     */
    public UserResponseDTO toDTO(List<Donation> donations) throws Exception {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail(this.getEmail());
        userResponseDTO.setPassword(this.getPassword());
        userResponseDTO.setName(decrypt(this.getName()));
        userResponseDTO.setAddress(decrypt(this.getAddress()));
        // Decrypt each donation and map it to DTO
        List<DonationDTO> decryptedDonations = donations.stream()
                .map(donation -> {
                    DonationDTO donationDTO = new DonationDTO();
                    try {
                        donationDTO.setAmount(decrypt(donation.getAmount()));
                        donationDTO.setDonationDate(donation.getDonationDate());
                    } catch (Exception e) {
                        // todo - handle exception
                    }
                    return donationDTO;
                })
                .collect(Collectors.toList());
        userResponseDTO.setDonationsDTO(decryptedDonations);
        userResponseDTO.setDonationsDTO(decryptedDonations);
        return userResponseDTO;
    }

}
