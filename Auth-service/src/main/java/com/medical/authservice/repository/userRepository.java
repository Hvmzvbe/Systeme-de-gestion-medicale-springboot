package com.medical.authservice.repository;

import com.medical.authservice.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface userRepository extends JpaRepository<user, Long> {
    Optional<user> findById(Long id);
    Optional<user> findByRole(String role);
}
