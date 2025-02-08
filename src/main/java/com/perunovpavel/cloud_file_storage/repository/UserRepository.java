package com.perunovpavel.cloud_file_storage.repository;

import com.perunovpavel.cloud_file_storage.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
