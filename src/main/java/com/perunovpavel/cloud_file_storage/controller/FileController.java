package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import com.perunovpavel.cloud_file_storage.service.core.impl.StorageServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Operations", description = "Endpoints for file management (upload, download, etc.).")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final StorageServiceImpl storageService;

    @Operation(
            summary = "Upload a single file",
            description = "Uploads one file optionally into the specified folder."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "File has been uploaded"),
            @ApiResponse(responseCode = "409", description = "File with same name already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<String> upload(
            @Parameter(description = "Folder name where the file will be placed (optional)")
            @RequestParam(value = "folder", required = false) String folder,
            @Parameter(description = "The file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        storageService.uploadFile(file, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body("File has been uploaded");
    }

    @Operation(
            summary = "Upload multiple files",
            description = "Uploads multiple files optionally into the specified folder."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Files have been uploaded"),
            @ApiResponse(responseCode = "409", description = "One of the files already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload-multiple")
    public ResponseEntity<String> uploadMultiple(
            @Parameter(description = "Folder name (optional)")
            @RequestParam(value = "folder", required = false) String folder,
            @Parameter(description = "List of files to upload", required = true)
            @RequestParam("files") List<MultipartFile> files) {
        storageService.uploadMultipleFiles(files, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body("Files has been uploaded");
    }

    @Operation(
            summary = "Download a file",
            description = "Downloads the specified file from optional folder."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(
            @Parameter(description = "File name to download")
            @PathVariable String filename,
            @Parameter(description = "Folder where file is located (optional)")
            @RequestParam(value = "folder", required = false) String folder) {
        Resource resource = storageService.downloadFile(filename, folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(
            summary = "Download multiple files as ZIP",
            description = "Downloads multiple files from optional folder, archived in ZIP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Files downloaded as ZIP"),
            @ApiResponse(responseCode = "404", description = "One or more files not found")
    })
    @GetMapping("/download-multiple")
    public ResponseEntity<Resource> downloadMultiple(
            @Parameter(description = "List of filenames to download")
            @RequestParam("files") List<String> files,
            @Parameter(description = "Folder (optional)")
            @RequestParam(value = "folder", required = false) String folder) {
        Resource zipResource = storageService.downloadMultipleFiles(files, folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"downloaded_files.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipResource);
    }

    @Operation(summary = "Delete a file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File has been deleted"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> delete(
            @Parameter(description = "File name to delete")
            @PathVariable String fileName,
            @Parameter(description = "Folder (optional)")
            @RequestParam(value = "folder", required = false) String folder) {
        storageService.deleteFile(fileName, folder);
        return ResponseEntity.ok("File has been deleted");
    }

    @Operation(summary = "Delete multiple files")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Files have been deleted"),
            @ApiResponse(responseCode = "404", description = "One or more files not found")
    })
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<String> deleteMultiple(
            @Parameter(description = "List of filenames to delete")
            @RequestParam("files") List<String> files,
            @Parameter(description = "Folder (optional)")
            @RequestParam(value = "folder", required = false) String folder) {
        storageService.deleteMultipleFiles(files, folder);
        return ResponseEntity.ok("Files has been deleted");
    }

    @Operation(summary = "Rename a file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File rename successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "409", description = "Conflict if new name already exists")
    })
    @PatchMapping("/{oldName}/rename")
    public ResponseEntity<String> rename(
            @Parameter(description = "Old file name")
            @PathVariable String oldName,
            @Parameter(description = "New file name")
            @RequestParam String newName,
            @Parameter(description = "Folder (optional)")
            @RequestParam(value = "folder", required = false) String folder) {
        storageService.renameFile(oldName, newName, folder);
        return ResponseEntity.ok("File rename successfully");
    }

    @Operation(summary = "Search for files by base name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "404", description = "Files with the same name isn't exists")
    })
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @Parameter(description = "Base name to search for")
            @RequestParam String name,
            @Parameter(description = "Folder (optional)")
            @RequestParam(value = "folder", required = false) String folder) {
        List<FileResponseDto> files = storageService.search(name, folder);
        return ResponseEntity.ok(files);
    }

}
