package com.perunovpavel.cloud_file_storage.repository;

import com.perunovpavel.cloud_file_storage.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByRoleName(String roleName);
}
