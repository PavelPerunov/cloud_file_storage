package com.perunovpavel.cloud_file_storage.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserAuthRequestDto",
        description = "Credentials required for user authentication.")
public class UserAuthRequestDto {

    @Email(message = "Некорректный формат email")
    @NotEmpty(message = "Email не может быть пустым")
    @Schema(description = "User's email address")
    private String email;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Schema(description = "User's password")
    private String password;
}
