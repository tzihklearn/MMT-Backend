package com.sipc.mmtbackend.utils.PictureUtil;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    // Minio API 地址
    private String endpoint;
    // Minio API 用户名（Access Key）
    private String accessKey;
    // Minio API 访问密钥
    private String secretKey;
    // 存储桶名称
    private String bucketName;
    // 公开访问的地址
    private String publicAddress;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}