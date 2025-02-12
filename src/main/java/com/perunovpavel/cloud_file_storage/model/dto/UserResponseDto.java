package com.perunovpavel.cloud_file_storage.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserResponseDto", description = "Information about user")
public class UserResponseDto {
    @Schema(description = "Unique identifier of the user")
    private Long id;

    @Schema(description = "Email address of the user")
    private String email;
}
