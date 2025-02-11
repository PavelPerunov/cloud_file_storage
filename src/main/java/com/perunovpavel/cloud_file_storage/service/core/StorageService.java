package com.perunovpavel.cloud_file_storage.service.core;

import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    void createMainUserFolder(Long userId);

    void createFolder(String name, String subfolder);

    void deleteFile(String fileName, String folder);

    void deleteMultipleFiles(List<String> fileNames, String folder);

    void deleteFolder(String folderName, String subFolder);

    void renameFile(String oldName, String newName, String folder);

    void renameFolder(String oldName, String newName, String folder);

    void uploadFile(MultipartFile file, String folder);

    void uploadMultipleFiles(List<MultipartFile> files, String folder);

    Resource downloadFile(String filename, String folder);

    Resource downloadMultipleFiles(List<String> files, String folder);

    List<FileResponseDto> search(String name, String folder);
}
