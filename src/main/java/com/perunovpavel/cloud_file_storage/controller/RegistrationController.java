package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.model.dto.UserRegisterRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Registration", description = "endpoint for registration")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @Operation(summary = "registration", description = "User registration in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "409", description = "User with same email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid request body (bad email format, etc.)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/registration")
    public ResponseEntity<?> registration(
            @Parameter(name = "Registration Credentials", required = true)
            @RequestBody @Valid UserRegisterRequestDto userDto) {

        UserResponseDto user = userService.registration(userDto);
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(user);
    }
}
