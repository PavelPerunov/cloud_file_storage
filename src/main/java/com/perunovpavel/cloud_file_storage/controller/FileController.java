package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import com.perunovpavel.cloud_file_storage.service.core.impl.StorageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final StorageServiceImpl storageService;

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file) {
        storageService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body("File has been uploaded");
    }

    @GetMapping
    public ResponseEntity<?> listAllFiles() {
        List<FileResponseDto> files = storageService.listAllFilesOfUser();
        return ResponseEntity.ok(files);
    }

    @GetMapping("folders/{folder}")
    public ResponseEntity<?> listFilesInFolder(@PathVariable String folder) {
        List<FileResponseDto> files = storageService.listFilesInSubfolder(folder);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = storageService.downloadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.ok("File has been deleted");
    }

    @DeleteMapping("/folders/{folderName}")
    public ResponseEntity<String> deleteFolder(@PathVariable String folderName) {
        storageService.deleteFolder(folderName);
        return ResponseEntity.ok("Folder has been deleted");
    }

    @PatchMapping("/{oldName}/rename")
    public ResponseEntity<String> renameFile(@PathVariable String oldName,
                                             @RequestParam String newName) {
        storageService.renameFile(oldName, newName);
        return ResponseEntity.ok("File rename successfully");
    }

    @PatchMapping("/folders/{oldName}/rename")
    public ResponseEntity<String> renameFolder(@PathVariable String oldName,
                                               @RequestParam String newName) {
        storageService.renameFolder(oldName, newName);
        return ResponseEntity.ok("Folder rename successfully");
    }

    @PostMapping("/folders")
    public ResponseEntity<String> createFolder(@RequestParam String folderName) {
        storageService.createFolder(folderName);
        return ResponseEntity.status(HttpStatus.CREATED).body("Folder has been created");
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {
        FileResponseDto file = storageService.search(name);
        return ResponseEntity.ok(file);
    }
}
