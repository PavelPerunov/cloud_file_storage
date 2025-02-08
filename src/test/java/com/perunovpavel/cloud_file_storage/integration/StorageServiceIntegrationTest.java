package com.perunovpavel.cloud_file_storage.integration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@SpringBootTest
@Testcontainers
public class StorageServiceIntegrationTest {

    @Container
    @ServiceConnection
    public static final GenericContainer<?> minioContainer = new GenericContainer<>("minio/minio:latest")
            .withExposedPorts(9090)
            .withEnv("MINIO_ROOT_USER", "test")
            .withEnv("MINIO_ROOT_PASSWORD", "test")
            .withCommand("server /data");

    @BeforeAll
    static void setUp() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9090))
                .credentials("test", "test")
                .build();

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("test-bucket").build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("test-bucket").build());
        }
    }

    @Test
    @Transactional
    void uploadFileShouldReturnExistsInMinio() {
        Long userId = 12L;

    }

}
