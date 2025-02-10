package com.perunovpavel.cloud_file_storage.model.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ErrorResponseDto {
    private int status;
    private String error;
    private String message;
}

