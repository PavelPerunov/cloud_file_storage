
package com.perunovpavel.cloud_file_storage.service.core;

import com.perunovpavel.cloud_file_storage.exception.InvalidRoleException;
import com.perunovpavel.cloud_file_storage.exception.UserNotFoundException;
import com.perunovpavel.cloud_file_storage.model.entity.Role;
import com.perunovpavel.cloud_file_storage.model.entity.User;
import com.perunovpavel.cloud_file_storage.repository.RoleRepository;
import com.perunovpavel.cloud_file_storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseUserService {

    protected final UserRepository userRepository;
    protected final RoleRepository roleRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<Role> findRoleByRoleName(String roleName) {
        return roleRepository.findRoleByRoleName(roleName);
    }

    protected User getUserByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с такой почтой не найден"));
    }

    protected Role getRoleByRoleName(String roleName) {
        return findRoleByRoleName(roleName)
                .orElseThrow(() -> new InvalidRoleException("Invalid role: " + roleName));
    }
}

