package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.model.dto.UserRegisterRequestDto;
import com.perunovpavel.cloud_file_storage.model.dto.UserResponseDto;
import com.perunovpavel.cloud_file_storage.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid UserRegisterRequestDto userDto) {
        UserResponseDto user = userService.registration(userDto);
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(user);
    }
}
