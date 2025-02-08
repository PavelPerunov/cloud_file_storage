package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.model.dto.UserAuthRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserAuthRequestDto userAuthRequestDto, HttpServletRequest request) {
        UserResponseDto user = authService.login(userAuthRequestDto, request);
        return ResponseEntity.ok().body(user);
    }

}
