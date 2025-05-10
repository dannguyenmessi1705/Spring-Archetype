package com.didan.archetype.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnProperty(
    value = {"app.cors.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@Configuration
@ConfigurationProperties(
    prefix = "app.cors" // Tiền tố cho các thuộc tính trong file cấu hình
)
@Primary
public class CorsConfig {
  private boolean enabled; // Biến này xác định xem cấu hình cors có được kích hoạt hay không

  @JsonProperty("allow-credentials")
  private boolean allowCredentials = true; // Cho phép cookie và thông tin xác thực khác được gửi trong yêu cầu CORS

  @JsonProperty("allow-headers")
  private String allowHeaders = "*"; // Các tiêu đề được phép trong yêu cầu CORS

  @JsonProperty("allow-methods")
  private String allowMethods = "*"; // Các phương thức được phép trong yêu cầu CORS

  @JsonProperty("allow-origin-patterns")
  private String allowOriginPatterns = "*";


  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**") // Áp dụng CORS cho tất cả các đường dẫn
            .allowedOriginPatterns(allowOriginPatterns) // Cho phép tất cả các nguồn gốc
            .allowedHeaders(allowHeaders) // Các tiêu đề được phép trong yêu cầu CORS
            .allowCredentials(allowCredentials) // Cho phép cookie và thông tin xác thực khác được gửi trong yêu cầu CORS
            .allowedMethods(allowMethods)
            .maxAge(3600); // Thời gian tối đa mà trình duyệt có thể lưu trữ thông tin CORS (3600 giây = 1 giờ)
      }
    };
  }
}
