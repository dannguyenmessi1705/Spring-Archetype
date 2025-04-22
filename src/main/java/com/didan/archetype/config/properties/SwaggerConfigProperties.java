package com.didan.archetype.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConfigurationProperties(
    prefix = "springdoc.api-docs" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "springdoc.api-docs"
)
@Configuration
@Primary

@Data
public class SwaggerConfigProperties {
  private boolean enabled = true; // Biến này xác định xem Swagger có được kích hoạt hay không
  private String title = "DiDanNguyen Service"; // Tiêu đề của tài liệu Swagger
  private String description = "DiDanNguyen Service API Documentation"; // Mô tả của tài liệu Swagger
  private String contactName = "DiDanNguyen"; // Tên liên hệ của người phát triển
  private String contactUrl = "https://iam.didan.id.vn"; // URL liên hệ của người phát triển
  private String version = "1.0.0"; // Phiên bản của tài liệu Swagger
}
