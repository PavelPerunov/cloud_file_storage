package com.perunovpavel.cloud_file_storage.controller;


import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import com.perunovpavel.cloud_file_storage.service.core.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {
    private final StorageService storageService;

    @GetMapping("/{folder}")
    public ResponseEntity<?> listFilesInFolder(@PathVariable String folder) {
        List<FileResponseDto> files = storageService.listFilesInSubfolder(folder);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{folderName}")
    public ResponseEntity<String> deleteFolder(@PathVariable String folderName) {
        storageService.deleteFolder(folderName);
        return ResponseEntity.ok("Folder has been deleted");
    }

    @PatchMapping("/{oldName}/rename")
    public ResponseEntity<String> renameFolder(@PathVariable String oldName,
                                               @RequestParam String newName) {
        storageService.renameFolder(oldName, newName);
        return ResponseEntity.ok("Folder rename successfully");
    }

    @PostMapping()
    public ResponseEntity<String> createFolder(@RequestParam String folderName) {
        storageService.createFolder(folderName);
        return ResponseEntity.status(HttpStatus.CREATED).body("Folder has been created");
    }

}
