package org.example.userapi.repository;

import org.example.userapi.model.Donation;
import org.example.userapi.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserDetail(UserDetail userDetail);

}