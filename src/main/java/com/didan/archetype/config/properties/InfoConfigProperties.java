package com.didan.archetype.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
    prefix = "info.build" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "info.build"
)
@RefreshScope // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@Data
public class InfoConfigProperties {
  @Value("${info.build.artifact}")
  private String artifact; // Tên artifact của ứng dụng

  @Value("${info.build.name}")
  private String name; // Tên ứng dụng

  @Value("${info.build.description}")
  private String description; // Mô tả ứng dụng

  @Value("${info.build.version}")
  private String version; // Phiên bản ứng dụng
}
