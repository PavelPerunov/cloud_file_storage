package com.perunovpavel.cloud_file_storage.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "FileResponseDto", description = "Information about file")
public class FileResponseDto {
    @Schema(description = "Path to file",
            example = "user-2-files/resources/pom.xml")
    private String path;
}
