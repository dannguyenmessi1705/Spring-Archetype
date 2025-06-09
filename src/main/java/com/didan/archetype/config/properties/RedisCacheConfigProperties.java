package com.didan.archetype.config.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "app.cache.redis" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "app.cache.redis"
)
 // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@Data
public class RedisCacheConfigProperties {

  private boolean enabled; // Biến này xác định xem cache có được kích hoạt hay không
  private long timeoutSeconds = 60L; // Thời gian timeout mặc định cho cache
  private int port = 6379; // Cổng mặc định của Redis
  private String host = "localhost"; // Địa chỉ host mặc định của Redis
  private List<String> nodes = new ArrayList<>(); // Danh sách các node Redis
  private Map<String, Long> cacheExpirations = new HashMap<>(); // Map chứa các thời gian hết hạn cho từng cache
  private String password = ""; // Mật khẩu cho Redis
  private long commandTimeoutSecond = 10;
  private int maxPoolSize = 8;
  private int maxIdle = 8;
  private int minIdle = 0;
}
