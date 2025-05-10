package com.didan.archetype.config.properties;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@Slf4j
@ConfigurationProperties(
    prefix = "app.minio" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "app.minio"
)
@ConditionalOnProperty(
    value = "app.minio.enabled", // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
public class MinioConfigProperties {
  private boolean enabled;

  private String host; // Địa chỉ host của MinIO

  private int port; // Cổng của MinIO

  private boolean secure;

  private String username; // Tên người dùng để đăng nhập vào MinIO

  private String password; // Mật khẩu để đăng nhập vào MinIO

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder()
        .endpoint(host, port, secure) // Địa chỉ host và cổng của MinIO
        .credentials(username, password) // Tên người dùng và mật khẩu để đăng nhập vào MinIO
        .build(); // Tạo một MinioClient với các thông tin đã cấu hình
  }

}
