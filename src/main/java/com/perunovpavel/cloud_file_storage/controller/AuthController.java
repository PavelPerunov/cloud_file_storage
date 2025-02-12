package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.model.dto.UserAuthRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authorization", description = "Endpoints for user authorization ")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "login", description = "User authorization in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Bad credentials"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(name = "Login Credentials", required = true)
            @RequestBody @Valid UserAuthRequestDto userAuthRequestDto, HttpServletRequest request) {
        UserResponseDto user = authService.login(userAuthRequestDto, request);
        return ResponseEntity.ok().body(user);
    }

}
