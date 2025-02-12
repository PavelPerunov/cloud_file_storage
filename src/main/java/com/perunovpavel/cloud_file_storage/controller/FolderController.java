package com.perunovpavel.cloud_file_storage.controller;


import com.perunovpavel.cloud_file_storage.service.core.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Folder Operations", description = "Endpoints for the folder management")
@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {
    private final StorageService storageService;

    @Operation(
            summary = "Create folder",
            description = "Creates folders (possibly inside another folder)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Folder created"),
            @ApiResponse(responseCode = "409", description = "Folder with the same name already exists"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping()
    public ResponseEntity<String> create(
            @Parameter(description = "Name of the folder to create")
            @RequestParam String folderName,
            @Parameter(description = "Optional parent folder")
            @RequestParam(value = "mainFolder", required = false) String mainFolder) {

        storageService.createFolder(folderName, mainFolder);
        return ResponseEntity.status(HttpStatus.CREATED).body("Folder has been created");
    }

    @Operation(summary = "Delete folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder deleted"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    @DeleteMapping("/{folderName}")
    public ResponseEntity<String> delete(
            @Parameter(description = "Folder name to delete")
            @PathVariable String folderName,
            @Parameter(description = "Subfolder (optional)")
            @RequestParam(value = "subFolder", required = false) String subFolder) {
        storageService.deleteFolder(folderName, subFolder);
        return ResponseEntity.ok("Folder has been deleted");
    }

    @Operation(summary = "Rename folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder rename successfully"),
            @ApiResponse(responseCode = "404", description = "Old folder not found"),
            @ApiResponse(responseCode = "409", description = "New folder name already exists")
    })
    @PatchMapping("/{oldName}/rename")
    public ResponseEntity<String> rename(
            @Parameter(description = "Old folder name")
            @PathVariable String oldName,
            @Parameter(description = "New folder name")
            @RequestParam String newName,
            @Parameter(description = "Optional folder prefix if needed")
            @RequestParam(value = "folder", required = false) String folder) {
        storageService.renameFolder(oldName, newName, folder);
        return ResponseEntity.ok("Folder rename successfully");
    }

}
