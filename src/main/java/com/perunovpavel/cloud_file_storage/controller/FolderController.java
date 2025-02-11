package com.perunovpavel.cloud_file_storage.controller;


import com.perunovpavel.cloud_file_storage.service.core.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {
    private final StorageService storageService;

    @PostMapping()
    public ResponseEntity<String> create(@RequestParam String folderName,
                                         @RequestParam(value = "mainFolder", required = false) String mainFolder) {
        storageService.createFolder(folderName, mainFolder);
        return ResponseEntity.status(HttpStatus.CREATED).body("Folder has been created");
    }

    @DeleteMapping("/{folderName}")
    public ResponseEntity<String> delete(@PathVariable String folderName,
                                         @RequestParam(value = "subFolder", required = false) String subFolder) {
        storageService.deleteFolder(folderName, subFolder);
        return ResponseEntity.ok("Folder has been deleted");
    }

    @PatchMapping("/{oldName}/rename")
    public ResponseEntity<String> rename(@PathVariable String oldName,
                                         @RequestParam String newName,
                                         @RequestParam(value = "folder", required = false) String folder) {
        storageService.renameFolder(oldName, newName,folder);
        return ResponseEntity.ok("Folder rename successfully");
    }

}
