package com.perunovpavel.cloud_file_storage.service.core.impl;

import com.perunovpavel.cloud_file_storage.exception.UserNotFoundException;
import com.perunovpavel.cloud_file_storage.model.entity.User;
import com.perunovpavel.cloud_file_storage.repository.UserRepository;
import com.perunovpavel.cloud_file_storage.service.core.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    public static final String BUCKET_NAME = "user-files";
    private final MinioClient minioClient;
    private final UserRepository userRepository;

    @Override
    @SneakyThrows
    public void putObject(String objectName,
                          InputStream inputStream,
                          long size,
                          @Nullable String contentType) {
        try {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(inputStream, size, -1);

            if (contentType != null) {
                builder.contentType(contentType);
            }

            minioClient.putObject(builder.build());
        } catch (Exception e) {
            throw new RuntimeException("Error putting object: " + objectName, e);
        }
    }


    @Override
    @SneakyThrows
    public void deleteObject(String filePath) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(filePath)
                .build());
    }

    @Override
    @SneakyThrows
    public GetObjectResponse getObject(String objectPath) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(objectPath)
                .build());
    }

    @Override
    public boolean isObjectExists(String objectName) {
        StatObjectResponse stat = getStatObjectResponse(objectName);
        return stat != null;
    }

    @Override
    @SneakyThrows
    public void copyObject(String newObjectPath, String oldObjectPath) {
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(newObjectPath)
                .source(CopySource.builder()
                        .bucket(BUCKET_NAME)
                        .object(oldObjectPath)
                        .build())
                .build());
    }

    @Override
    public Iterable<Result<Item>> listObjects(String path) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(BUCKET_NAME)
                .prefix(path)
                .recursive(true)
                .build());
    }

    private StatObjectResponse getStatObjectResponse(String prefix) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(prefix)
                    .build());
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equalsIgnoreCase("NoSuchKey")) {
                return null;
            }
            throw new RuntimeException("Error checking file/folder existence: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when checking file/folder", e);
        }
    }

    public Long getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail((String) authentication.getPrincipal()).orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getId();
    }

    @NotNull
    public static String buildUserPrefix(Long userId) {
        return "user-" + userId + "-files/";
    }
}
