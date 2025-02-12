package com.perunovpavel.cloud_file_storage.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "ErrorResponseDto", description = "Information about error")
public class ErrorResponseDto {
    @Schema(description = "HTTP status code of the error",
            example = "404")
    private int status;
    @Schema(description = "Short reason phrase associated with the status code",
            example = "Not Found")
    private String error;
    @Schema(description = "Detailed error message or reason of the failure",
            example = "File with the given name was not found on the server.")
    private String message;
}

