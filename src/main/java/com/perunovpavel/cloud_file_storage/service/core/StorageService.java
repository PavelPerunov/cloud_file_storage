package com.perunovpavel.cloud_file_storage.service.core;

import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    void createMainUserFolder(Long userId);

    void createFolder(String name);

    void deleteFile(String fileName);

    void deleteFolder(String folderName);

    void renameFile(String oldName, String newName);

    void renameFolder(String oldName, String newName);

    void uploadFile(MultipartFile file);

    Resource downloadFile(String filename);


    List<FileResponseDto> listAllFilesOfUser();

    List<FileResponseDto> listFilesInSubfolder(String subfolder);


    FileResponseDto search(String name);
}
