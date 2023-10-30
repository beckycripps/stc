package org.example.userapi.repository;

import org.example.userapi.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface UserRepository extends JpaRepository<UserDetail, Long> {
    /**
     * Finds a UserDetail entity by the provided email.
     *
     * @param email The email address associated with the UserDetail to be retrieved.
     * @return The UserDetail entity corresponding to the provided email, or null if not found.
     */
    UserDetail findByEmail(String email);

}