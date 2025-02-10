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

    @GetMapping
    public ResponseEntity<?> listAll() {
        List<FileResponseDto> files = storageService.listAllFilesOfUser();
        return ResponseEntity.ok(files);
    }

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam(value = "folder", required = false) String folder,
                                         @RequestParam("file") MultipartFile file) {
        storageService.uploadFile(file, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body("File has been uploaded");
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<String> uploadMultiple(@RequestParam(value = "folder", required = false) String folder,
                                                 @RequestParam("files") List<MultipartFile> files) {
        storageService.uploadMultipleFiles(files, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body("Files has been uploaded");
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> delete(@PathVariable String fileName,
                                         @RequestParam(value = "folder", required = false) String folder) {
        storageService.deleteFile(fileName, folder);
        return ResponseEntity.ok("File has been deleted");
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<String> deleteMultiple(@RequestParam("files") List<String> files,
                                                 @RequestParam(value = "folder", required = false) String folder) {
        storageService.deleteMultipleFiles(files, folder);
        return ResponseEntity.ok("Files has been deleted");
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename,
                                             @RequestParam(value = "folder", required = false) String folder) {
        Resource resource = storageService.downloadFile(filename, folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/download-multiple")
    public ResponseEntity<Resource> downloadMultiple(@RequestParam("files") List<String> files) {
        return null;
    }

    @PatchMapping("/{oldName}/rename")
    public ResponseEntity<String> rename(@PathVariable String oldName,
                                         @RequestParam String newName) {
        storageService.renameFile(oldName, newName);
        return ResponseEntity.ok("File rename successfully");
    }


    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {
        FileResponseDto file = storageService.search(name);
        return ResponseEntity.ok(file);
    }
}
