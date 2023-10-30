package org.example.userapi.model;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private String password;
    private String address;
    private List<DonationDTO> donationsDTO;
}



