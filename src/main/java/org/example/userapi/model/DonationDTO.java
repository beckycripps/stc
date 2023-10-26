package org.example.userapi.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DonationDTO {
    private String amount;

    private LocalDateTime donationDate;
}
