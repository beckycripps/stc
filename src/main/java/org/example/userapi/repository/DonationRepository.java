package org.example.userapi.repository;

import org.example.userapi.model.Donation;
import org.example.userapi.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 */
public interface DonationRepository extends JpaRepository<Donation, Long> {
    /**
     * Finds a list of Donation entities associated with the given UserDetail.
     *
     * @param userDetail The UserDetail entity for which donations need to be retrieved.
     * @return List of Donation entities associated with the provided UserDetail.
     */
    List<Donation> findByUserDetail(UserDetail userDetail);

}