package com.perunovpavel.cloud_file_storage.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "UserRegisterRequestDto", description = "Credentials required for user registration")
public class UserRegisterRequestDto {

    @Email(message = "Некорректный формат email")
    @NotEmpty(message = "Email не может быть пустым")
    @Schema(description = "User's email address")
    private String email;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Schema(description = "User's password")
    private String password;
}
