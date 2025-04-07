package com.didan.archetype.config.properties;

import com.didan.archetype.config.properties.supobj.CaffeineCacheConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(
    prefix = "app.cache.memory" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "app.cache.memory"
)
 // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@Data
public class LocalCacheConfigProperties {
  private boolean enabled; // Biến này xác định xem cache có được kích hoạt hay không
  private String cacheNames; // Tên của cache
  @NestedConfigurationProperty // Annotation này cho phép ánh xạ các thuộc tính lồng nhau
  private CaffeineCacheConfig caffeine; // Các thuộc tính cấu hình cho cache Caffeine
}
