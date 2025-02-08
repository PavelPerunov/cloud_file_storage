package com.perunovpavel.cloud_file_storage.integration;

import com.perunovpavel.cloud_file_storage.exception.UserAlreadyExistsException;
import com.perunovpavel.cloud_file_storage.model.dto.UserRegisterRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.repository.UserRepository;
import com.perunovpavel.cloud_file_storage.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDB")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void registrationShouldReturnCreatedUser() {
        UserRegisterRequestDto userRequestDto = new UserRegisterRequestDto();
        userRequestDto.setEmail("test666@test.com");
        userRequestDto.setPassword("password123");

        UserResponseDto userResponseDto = userService.registration(userRequestDto);

        assertNotNull(userResponseDto);
        assertEquals("test666@test.com", userResponseDto.getEmail());
    }

    @Test
    @Transactional
    void registrationShouldThrowAlreadyExists() {
        UserRegisterRequestDto userRequestDto = new UserRegisterRequestDto();
        userRequestDto.setEmail("test666@test.com");
        userRequestDto.setPassword("password123");

        userService.registration(userRequestDto);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registration(userRequestDto));
    }
}