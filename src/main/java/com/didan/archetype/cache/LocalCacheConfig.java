package com.didan.archetype.cache;

import com.didan.archetype.config.properties.LocalCacheConfigProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration // Đánh dấu lớp này là một cấu hình Spring
@EnableConfigurationProperties({LocalCacheConfigProperties.class}) // Kích hoạt các thuộc tính cấu hình từ LocalCacheConfigProperties
@ConditionalOnProperty(
    value = {"app.cache.memory.enabled"}, // Chỉ định rằng cấu hình này sẽ được kích hoạt nếu thuộc tính "app.cache.memory.enabled" có giá trị là "true"
    havingValue = "true"
)
@Slf4j
public class LocalCacheConfig {
  @Bean
  public Caffeine<Object, Object> caffeineCacheBuilder(LocalCacheConfigProperties configs) {
    return Caffeine.from(configs.getCaffeine().getSpec()); // Tạo một builder cho cache Caffeine từ các thuộc tính cấu hình
  }

  @Bean
  @Primary // Đánh dấu bean này là chính, sẽ được sử dụng thay thế cho các bean khác cùng loại
  public CacheManager localCacheManager(Caffeine<Object, Object> caffeineCacheBuilder, LocalCacheConfigProperties configs) {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    if (configs.getCacheNames() != null && !configs.getCacheNames().isEmpty()) {
      cacheManager.setCacheNames(List.of(configs.getCacheNames())); // Nếu có tên cache, thiết lập tên cache cho cache manager
    } else {
      cacheManager.setCacheNames((Collection)null); // Nếu không có tên cache, thiết lập là null
    } // Thiết lập tên cache cho cache manager

    log.info("Configuring local cache manager");
    log.info("Using caffeine: {}", caffeineCacheBuilder.toString());
    log.info("Cache names: {}", configs.getCacheNames());
    cacheManager.setCaffeine(caffeineCacheBuilder);
    return cacheManager;
  }
}
