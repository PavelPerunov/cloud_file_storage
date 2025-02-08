package com.perunovpavel.cloud_file_storage.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequestDto {

    @Email(message = "Некорректный формат email")
    @NotEmpty(message = "Email не может быть пустым")
    private String email;

    @NotEmpty(message = "Пароль не может быть пустым")
    private String password;
}
