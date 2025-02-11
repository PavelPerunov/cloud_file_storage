package com.perunovpavel.cloud_file_storage.service.core.impl;

import com.perunovpavel.cloud_file_storage.exception.FileAlreadyExistsException;
import com.perunovpavel.cloud_file_storage.exception.FolderAlreadyExistsException;
import com.perunovpavel.cloud_file_storage.exception.FolderNotFoundException;
import com.perunovpavel.cloud_file_storage.model.dto.FileResponseDto;
import com.perunovpavel.cloud_file_storage.service.core.StorageService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final MinioServiceImpl minioService;

    @Override
    public Resource downloadFile(String filename, String folder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String filePath = buildPath(userId, folder) + filename;

        if (!minioService.isObjectExists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found " + filePath);
        }

        try {
            GetObjectResponse minioFile = minioService.getObject(filePath);
            return new InputStreamResource(minioFile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when downloading file : " + e.getMessage());
        }
    }

    @Override
    public Resource downloadMultipleFiles(List<String> files, String folder) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (String file : files) {
                Resource resource = downloadFile(file, folder);

                ZipEntry zipEntry = new ZipEntry(file);
                zipOutputStream.putNextEntry(zipEntry);
                resource.getInputStream().transferTo(zipOutputStream);
                zipOutputStream.closeEntry();
            }

            zipOutputStream.finish();

            return new ByteArrayResource(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when downloading multiple files: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName, String folder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String filePath = buildPath(userId, folder) + fileName;

        if (!minioService.isObjectExists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filePath);
        }

        try {
            minioService.deleteObject(filePath);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when deleting file : " + e.getMessage());
        }
    }

    @Override
    public void deleteMultipleFiles(List<String> fileNames, String folder) {
        for (String fileName : fileNames) {
            deleteFile(fileName, folder);
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
    public void createFolder(String name, String mainFolder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String folderName = buildPath(userId, mainFolder) + name + "/";
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
    public void deleteFolder(String folderName, String subFolder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String folderPath = buildPath(userId, subFolder) + (folderName.endsWith("/") ? folderName : folderName + "/");

        if (!minioService.isObjectExists(folderPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found");
        }
        try {
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
    public void uploadFile(MultipartFile file, String folder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String filePath = buildPath(userId, folder) + file.getOriginalFilename();
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
    public void uploadMultipleFiles(List<MultipartFile> files, String folder) {
        for (MultipartFile file : files) {
            uploadFile(file, folder);
        }
    }

    @Override
    public void renameFile(String oldName, String newName, String folder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String oldFilePath = buildPath(userId, folder) + oldName;
        String newFilePath = buildPath(userId, folder) + newName;

        if (!minioService.isObjectExists(oldFilePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        if (minioService.isObjectExists(newFilePath)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "File " + newName + " already exists");
        }
        try {

            minioService.copyObject(newFilePath, oldFilePath);
            minioService.deleteObject(oldFilePath);

        } catch (Exception e) {
            throw new RuntimeException("Error RENAMING file: " + e.getMessage(), e);
        }
    }

    @Override
    public void renameFolder(String oldName, String newName, String folder) {
        Long userId = minioService.getUserIdFromSecurityContext();
        String oldFolderPath = buildPath(userId, folder) + oldName + "/";
        String newFolderPath = buildPath(userId, folder) + newName + "/";

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
            deleteFolder(oldName, folder);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<FileResponseDto> search(String name, String folder) {

        Long userId = minioService.getUserIdFromSecurityContext();
        String prefix = buildPath(userId, folder);

        List<FileResponseDto> files = new ArrayList<>();
        Iterable<Result<Item>> items = minioService.listObjects(prefix);

        for (Result<Item> result : items) {
            try {
                Item item = result.get();
                String objectName = item.objectName();
                String relativeName = objectName.substring(prefix.length());

                if (relativeName.endsWith("/")) {
                    continue;
                }

                String baseName = getBaseName(relativeName);

                if (baseName.equals(name)) {
                    files.add(new FileResponseDto(relativeName));
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        if (files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Files with the same name isn't exists");
        }
        return files;
    }

    private String getBaseName(String filename) {
        int lastSlashIndex = filename.lastIndexOf('/');
        String justName = (lastSlashIndex != -1)
                ? filename.substring(lastSlashIndex + 1)
                : filename;

        int dotIndex = justName.lastIndexOf('.');
        if (dotIndex == -1) {
            return justName;
        } else {
            return justName.substring(0, dotIndex);
        }
    }

    private String buildPath(Long userId, String folder) {
        return MinioServiceImpl.buildUserPrefix(userId)
               + (folder != null ? folder + "/" : "");
    }
}
