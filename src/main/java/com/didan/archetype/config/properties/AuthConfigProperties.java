package com.didan.archetype.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConfigurationProperties(
    prefix = "app.auth" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "app.auth"
)
@Configuration
@Primary
@RefreshScope // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@ConditionalOnProperty(
    value = {"app.auth.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@Data
public class AuthConfigProperties {
  private boolean enabled; // Biến này xác định xem cấu hình auth có được kích hoạt hay không

  @Value("${app.auth.type}")
  private Type type; // Loại auth KEY - được định nghĩa trong enum Type

  @Autowired(
    required = false
  )
  private AuthTypePublicKey key;

  public static enum Type {
    KEY;
    private Type() {}
  }

  @Configuration
  @ConditionalOnProperty(
      value = "app.auth.type",
      havingValue = "KEY" // Điều kiện để kích hoạt cấu hình này
  )
  @RefreshScope // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
  @Data
  public static class AuthTypePublicKey {
    @Value("${app.auth.key.public-key}")
    private String publicKey; // Khóa công khai được sử dụng trong auth
  }
}
