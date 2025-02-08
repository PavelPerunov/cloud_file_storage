package com.perunovpavel.cloud_file_storage.service.core.impl;

import com.perunovpavel.cloud_file_storage.exception.FileAlreadyExistsException;
import com.perunovpavel.cloud_file_storage.exception.FolderAlreadyExistsException;
import com.perunovpavel.cloud_file_storage.exception.FolderNotFoundException;
import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import com.perunovpavel.cloud_file_storage.service.core.StorageService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final MinioServiceImpl minioService;

    @Override
    public Resource downloadFile(String filename) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String filePath = MinioServiceImpl.buildUserPrefix(userId) + filename;

        try {

            if (!minioService.isObjectExists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }

            GetObjectResponse minioFile = minioService.getObject(filePath);

            return new InputStreamResource(minioFile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when downloading file : " + e.getMessage());
        }
    }

    @Override
    public void createMainUserFolder(Long userId) {
        String prefix = MinioServiceImpl.buildUserPrefix(userId);
        try {
            minioService.putObject(prefix, new ByteArrayInputStream(new byte[0]), 0, null);
        } catch (Exception e) {
            throw new RuntimeException("Error creating  main user folder", e);
        }
    }

    @Override
    public void createFolder(String name) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String folderName = MinioServiceImpl.buildUserPrefix(userId) + name + "/";
        String fileName = MinioServiceImpl.buildUserPrefix(userId) + name;
        if (minioService.isObjectExists(folderName)) {
            throw new FolderAlreadyExistsException("Folder with the same name already exists");
        }
        if (minioService.isObjectExists(fileName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "File with the same name already exists (cannot create folder)");
        }
        try {
            minioService.putObject(folderName, new ByteArrayInputStream(new byte[0]), 0, null);
        } catch (Exception e) {
            throw new RuntimeException("Error creating folder", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String filePath = MinioServiceImpl.buildUserPrefix(userId) + fileName;
        try {

            if (!minioService.isObjectExists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }

            minioService.deleteObject(filePath);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when deleting file : " + e.getMessage());
        }
    }

    @Override
    public void deleteFolder(String folderName) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String folderPath = MinioServiceImpl.buildUserPrefix(userId) + (folderName.endsWith("/") ? folderName : folderName + "/");
        try {
            if (!minioService.isObjectExists(folderPath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found");
            }

            Iterable<Result<Item>> results = minioService.listObjects(folderPath);
            for (Result<Item> result : results) {
                Item item = result.get();
                minioService.deleteObject(item.objectName());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when deleting folder : " + e.getMessage());
        }
    }

    @Override
    public void uploadFile(MultipartFile file) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String filePath = MinioServiceImpl.buildUserPrefix(userId) + file.getOriginalFilename();
        String folderPath = filePath + "/";

        if (minioService.isObjectExists(folderPath)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Folder with the same name already exists (CAN'T create file)");
        }

        if (minioService.isObjectExists(filePath)) {
            throw new FileAlreadyExistsException("File with the same name already exists");
        }
        try {
            minioService.putObject(filePath, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    @Override
    public void renameFile(String oldName, String newName) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String oldFilePath = MinioServiceImpl.buildUserPrefix(userId) + oldName;
        String newFilePath = MinioServiceImpl.buildUserPrefix(userId) + newName;

        try {
            if (!minioService.isObjectExists(oldFilePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }

            if (minioService.isObjectExists(newFilePath)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "File " + newName + " already exists");
            }

            minioService.copyObject(newFilePath, oldFilePath);
            minioService.deleteObject(oldName);

        } catch (Exception e) {
            throw new RuntimeException("Error RENAMING file: " + e.getMessage(), e);
        }
    }

    @Override
    public void renameFolder(String oldName, String newName) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String oldFolderPath = MinioServiceImpl.buildUserPrefix(userId) + oldName + "/";
        String newFolderPath = MinioServiceImpl.buildUserPrefix(userId) + newName + "/";

        if (!minioService.isObjectExists(oldFolderPath)) {
            throw new FolderNotFoundException("Folder with same name not found");
        }

        if (minioService.isObjectExists(newFolderPath)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Folder " + newName + " already exists");
        }

        Iterable<Result<Item>> results = minioService.listObjects(oldFolderPath);
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                String oldObjectName = item.objectName();
                String newObjectName = oldObjectName.replaceFirst(oldFolderPath, newFolderPath);
                minioService.copyObject(newObjectName, oldObjectName);
            }
            deleteFolder(oldFolderPath);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<FileResponseDto> listAllFilesOfUser() {
        Long userId = minioService.getUserIdFromSecurityContext();
        String prefix = MinioServiceImpl.buildUserPrefix(userId);

        return listObjects(prefix);
    }

    @Override
    public List<FileResponseDto> listFilesInSubfolder(String subfolder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String prefix = MinioServiceImpl.buildUserPrefix(userId);
        String fullPrefix = prefix + (subfolder.endsWith("/") ? subfolder : subfolder + "/");

        return listObjects(fullPrefix);
    }

    @Override
    public FileResponseDto search(String name) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String prefixFile = MinioServiceImpl.buildUserPrefix(userId) + name;
        String prefixFolder = MinioServiceImpl.buildUserPrefix(userId) + name + "/";

        boolean fileExists = minioService.isObjectExists(prefixFile);
        boolean folderExists = minioService.isObjectExists(prefixFolder);

        if (fileExists && folderExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Error: Both file and folder with name = '" + name + "' exist");
        }

        if (fileExists) {
            return new FileResponseDto(name);
        }
        if (folderExists) {
            return new FileResponseDto(name);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No file or folder found with name = '" + name + "'");
    }

    private List<FileResponseDto> listObjects(String path) {
        List<FileResponseDto> files = new ArrayList<>();

        if (!minioService.isObjectExists(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder: " + path + " not found");
        }

        Iterable<Result<Item>> results = minioService.listObjects(path);

        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                String objectName = item.objectName();

                if (objectName.equals(path)) {
                    continue;
                }

                files.add(new FileResponseDto(objectName));
            } catch (Exception e) {
                throw new RuntimeException("Error retrieving file list", e);
            }
        }
        return files;
    }
}
