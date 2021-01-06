package com.nphc.swe.persistence.repo;

import com.nphc.swe.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);

}
