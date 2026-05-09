package com.pori.user.repository;

import com.pori.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByHandle(String handle);
    boolean existsByEmail(String email);
    boolean existsByHandle(String handle);
}
