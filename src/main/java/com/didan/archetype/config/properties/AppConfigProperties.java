package com.didan.archetype.config.properties;

import com.zaxxer.hikari.HikariConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration // Đánh dấu lớp này là một cấu hình Spring
@ConfigurationProperties(prefix = "app") // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "app"
 // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@Data
public class AppConfigProperties {
  @Value("${app.application-short-name}") // Đọc giá trị từ file cấu hình với key là "app.application.short-name")
  private String applicationShortName;

  @Value("${app.application-context-name}")
  private String applicationContextName;

  @Value("${app.log-request-http:#{true}}")
  private boolean logRequestHttp = true;

  @Value("${app.default-service-enable-log-request:#{true}}")
  private boolean defaultServiceEnableLogRequest = true;

  @Value("${app.repository-query-limit-warning-ms:30}") // Chỉ định thời gian cảnh báo cho truy vấn repository
  private int repositoryQueryLimitWarningMs;

  private int asyncExecutorCorePoolSize = 2; // Kích thước pool tối thiểu của executor bất đồng bộ

  private int asyncExecutorMaxPoolSize = 4; // Kích thước pool tối đa của executor bất đồng bộ

  private List<String> localeResolverLanguages = Arrays.asList("en", "vi"); // Danh sách các ngôn ngữ hỗ trợ

  private String defaultLanguage = "vi"; // Ngôn ngữ mặc định

  @Value("${app.time-trace-enabled:#{false}}") // Đọc giá trị từ file cấu hình với key là "app.time-trace-enabled"
  private boolean timeTraceEnabled;

  private String asyncExecutorThreadNamePrefix = "Async-"; // Tiền tố cho tên luồng của executor bất đồng bộ

  private Map<String, BaseDataSourceProperties> datasource; // Map chứa các thuộc tính của datasource

  @EqualsAndHashCode(callSuper = true)
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class BaseDataSourceProperties extends DataSourceProperties {

    private Map<String, String> properties = new HashMap<>(); // Map chứa các thuộc tính của datasource
    private HikariConfig configuration; // Cấu hình HikariCP
  }
}
