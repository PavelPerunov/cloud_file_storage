package com.perunovpavel.cloud_file_storage.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.url}")
    private String url;

    @Value("${minio.credentials.user}")
    private String minioUser;

    @Value("${minio.credentials.password}")
    private String minioPassword;

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(url)
                .credentials(minioUser, minioPassword)
                .build();
    }
}
