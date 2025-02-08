package com.perunovpavel.cloud_file_storage.service;

import com.perunovpavel.cloud_file_storage.model.entity.Role;
import com.perunovpavel.cloud_file_storage.model.entity.User;
import com.perunovpavel.cloud_file_storage.model.mapper.UserMapper;
import com.perunovpavel.cloud_file_storage.repository.RoleRepository;
import com.perunovpavel.cloud_file_storage.repository.UserRepository;
import com.perunovpavel.cloud_file_storage.service.core.impl.StorageServiceImpl;
import com.perunovpavel.cloud_file_storage.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaseUserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private StorageServiceImpl storageService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;


    @Test
    void findByEmailShouldReturnUserIfExists() {
        User user = new User();
        user.setId(123L);
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(123L, result.get().getId());
        assertEquals("test@example.com", result.get().getEmail());

        Mockito.verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmailShouldReturnEmptyIfUserNotExists() {
        User user = new User();
        user.setId(123L);
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("missing@example.com");

        assertTrue(result.isEmpty());

        Mockito.verify(userRepository).findByEmail("missing@example.com");

    }

    @Test
    void findRoleByRoleNameShouldReturnRoleIfExists() {
        Role role = new Role();
        role.setId(21L);
        role.setRoleName("MANAGER");

        Mockito.when(roleRepository.findRoleByRoleName("MANAGER"))
                .thenReturn(Optional.of(role));

        Optional<Role> result = userService.findRoleByRoleName("MANAGER");

        assertTrue(result.isPresent());
        assertEquals(21L, result.get().getId());
        assertEquals("MANAGER", result.get().getRoleName());

        Mockito.verify(roleRepository).findRoleByRoleName("MANAGER");
    }

    @Test
    void findRoleByRoleNameShouldReturnEmptyIfRoleNotExist() {
        Role role = new Role();
        role.setId(21L);
        role.setRoleName("MANAGER");

        Mockito.when(roleRepository.findRoleByRoleName("MANAGER"))
                .thenReturn(Optional.empty());

        Optional<Role> result = userService.findRoleByRoleName("MANAGER");

        assertTrue(result.isEmpty());

        Mockito.verify(roleRepository).findRoleByRoleName("MANAGER");
    }

}