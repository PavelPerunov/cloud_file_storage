package com.perunovpavel.cloud_file_storage.service.core;

import io.minio.GetObjectResponse;
import io.minio.Result;
import io.minio.messages.Item;
import org.jetbrains.annotations.Nullable;


import java.io.InputStream;

public interface MinioService {

    void putObject(String objectName,
                   InputStream inputStream,
                   long size,
                   @Nullable String contentType);

    void deleteObject(String objectName);

    boolean isObjectExists(String objectName);

    GetObjectResponse getObject(String objectPath);

    void copyObject(String newFilePath, String oldFilePath);

    Iterable<Result<Item>> listObjects(String path);

}
