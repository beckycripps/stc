package org.example.userapi.model;

import org.example.userapi.repository.DonationRepository;
import org.example.userapi.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertThrows;

@DataJpaTest
public class DonationRepositoryTest {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveDonation() {
        // Create and save a UserDetail first
        UserDetail userDetail = new UserDetail();
        // Set userDetail properties...
        userRepository.save(userDetail);
        Donation donation = new Donation();
        donation.setAmount("100");
        donation.setDonationDate(LocalDateTime.now());
        donation.setUserDetail(userDetail); // Set the userDetail association


        Donation savedDonation = donationRepository.save(donation);

        Assert.assertNotNull(savedDonation.getId());
        Assert.assertSame("100", savedDonation.getAmount());
        Assert.assertNotNull(savedDonation.getDonationDate());
    }

    @Test
    public void testFindDonationsByUserDetail() {
        // Create a UserDetail and save it to the database
        UserDetail userDetail = new UserDetail();
        // Set userDetail properties...
        userDetail = userRepository.save(userDetail);

        // Create and save donations associated with the UserDetail
        Donation donation1 = new Donation();
        donation1.setAmount("50");
        donation1.setDonationDate(LocalDateTime.now());
        donation1.setUserDetail(userDetail);
        donationRepository.save(donation1);

        Donation donation2 = new Donation();
        donation2.setAmount("75");
        donation2.setDonationDate(LocalDateTime.now());
        donation2.setUserDetail(userDetail);
        donationRepository.save(donation2);

        // Retrieve donations associated with the UserDetail
        List<Donation> donations = donationRepository.findByUserDetail(userDetail);

        Assert.assertNotNull(donations);
        Assert.assertEquals(2, donations.size());
    }

    @Test
    public void testValidationConstraints() {
        // Create and save a UserDetail first
        UserDetail userDetail = new UserDetail();
        // Set userDetail properties...
        userDetail = userRepository.save(userDetail);

        // Attempt to save a Donation with null amount and donationDate
        Donation donation = new Donation();
        donation.setUserDetail(userDetail); // Set the saved UserDetail
        donation.setAmount(null); // Set amount to null intentionally to trigger validation error
        donation.setDonationDate(null); // Set donationDate to null intentionally to trigger validation error

        // Catch DataIntegrityViolationException and assert the nested ConstraintViolationException
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> donationRepository.save(donation));
        Assert.assertTrue(exception.getCause() instanceof ConstraintViolationException);

    }

}
