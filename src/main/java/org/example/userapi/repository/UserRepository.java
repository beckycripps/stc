package org.example.userapi.repository;

import org.example.userapi.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetail, Long> {
    UserDetail findByEmail(String email);

}