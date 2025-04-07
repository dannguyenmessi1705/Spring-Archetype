package com.didan.archetype.cache;

import com.didan.archetype.config.properties.RedisCacheConfigProperties;
import io.lettuce.core.ReadFrom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Configuration // Đánh dấu lớp này là một cấu hình Spring
@EnableConfigurationProperties({RedisCacheConfigProperties.class}) // Kích hoạt ánh xạ các thuộc tính từ lớp RedisCacheConfigProperties
@ConditionalOnProperty(
    value = {"app.cache.redis.enabled"},
    havingValue = "true"
) // Chỉ định rằng cấu hình này sẽ được kích hoạt nếu thuộc tính "app.cache.redis.enabled" có giá trị là "true"
@Slf4j
public class RedisCacheConfig {

  @Value("${app.application-short-name}") // Lấy giá trị của thuộc tính "app.application-short-name" từ file cấu hình)
  private String applicationShortName;

  private RedisCacheConfiguration createCacheConfiguration(long timeoutSeconds) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader(); // Lấy ClassLoader hiện tại
    JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader); // Tạo một serializer để tuần tự hóa đối tượng
    RedisSerializationContext.SerializationPair<Object> pair = SerializationPair.fromSerializer(jdkSerializer); // Tạo một cặp tuần tự hóa từ serializer
    return RedisCacheConfiguration
        .defaultCacheConfig() // Tạo cấu hình cache mặc định
        .serializeValuesWith(pair) // Thiết lập cách tuần tự hóa giá trị cache
        .computePrefixWith((cacheName) -> {
          String prefix = applicationShortName + "::";
          if (StringUtils.hasText(cacheName)) {
            prefix += cacheName + "::"; // Nếu tên cache không rỗng, thêm tên cache vào prefix
          }
          return prefix; // Trả về prefix cho cache
        }).entryTtl(Duration.ofSeconds(timeoutSeconds)); // Thiết lập thời gian sống cho cache
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory(RedisCacheConfigProperties configs) {
    log.info("Redis (/Lettuce) configuration enabled. With cache timeout " + configs.getTimeoutSeconds() + " seconds.");
    if (!CollectionUtils.isEmpty(configs.getNodes())) { // Nếu danh sách các node không rỗng
      log.info("Redis cluster: {}", configs.getNodes());
      LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder().readFrom(ReadFrom.MASTER).build(); // Tạo cấu hình client Lettuce với chế độ đọc từ master
      RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(configs.getNodes()); // Tạo cấu hình cluster Redis từ danh sách các node
      if (StringUtils.hasText(configs.getPassword())) { // Nếu có mật khẩu
        redisClusterConfiguration.setPassword(RedisPassword.of(configs.getPassword())); // Thiết lập mật khẩu cho cấu hình cluster
      }
      return new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration); // Trả về kết nối Lettuce với cấu hình cluster
    } else {
      log.info("Redis standalone: {}:{}", configs.getHost(), configs.getPort());
      RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(); // Tạo cấu hình standalone Redis
      redisStandaloneConfiguration.setHostName(configs.getHost()); // Thiết lập hostname
      redisStandaloneConfiguration.setPort(configs.getPort()); // Thiết lập cổng
      if (StringUtils.hasText(configs.getPassword())) {  // Nếu có mật khẩu
        redisStandaloneConfiguration.setPassword(RedisPassword.of(configs.getPassword())); // Thiết lập mật khẩu
      }
      return new LettuceConnectionFactory(redisStandaloneConfiguration); // Trả về kết nối Lettuce với cấu hình standalone
    }
  }

  @Bean
  @Primary
  public RedisTemplate<Byte[], Byte[]> redisTemplate(RedisConnectionFactory cnf) {
    RedisTemplate<Byte[], Byte[]> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(cnf);
    return redisTemplate;
  } // Tạo một RedisTemplate với kiểu dữ liệu là Byte[] cho cả key và value, mặc định là chính (Primary) để sử dụng trong toàn bộ ứng dụng
  // Khi sử dụng chỉ cần khai báo private final RedisTemplate<Byte[], Byte[]> redisTemplate; trong class cần sử dụng là có thể sử dụng được

  @Bean({"RedisTemplateByteByte"})
  public RedisTemplate<Byte[], Byte[]> redisTemplateByteByte(RedisConnectionFactory cnf) {
    RedisTemplate<Byte[], Byte[]> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(cnf);
    return redisTemplate;
  } // Tạo một RedisTemplate với kiểu dữ liệu là Byte[] cho cả key và value, không phải là chính (Primary) để sử dụng trong toàn bộ ứng dụng
  // Khi sử dụng, thêm @Qualifier("RedisTemplateByteByte") private final RedisTemplate<Byte[], Byte[]> redisTemplate; vào class cần sử dụng là có thể sử dụng được

  @Bean({"RedisTemplateStringObject"})
  public RedisTemplate<String, Object> redisTemplateStringObject(RedisConnectionFactory cnf) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(cnf);
    return redisTemplate;
  } // Tạo một RedisTemplate với kiểu dữ liệu là String cho key và Object cho value, không phải là chính (Primary) để sử dụng trong toàn bộ ứng dụng
  // Khi sử dụng, thêm @Qualifier("RedisTemplateStringObject") private final RedisTemplate<String, Object> redisTemplate; vào class cần sử dụng là có thể sử dụng được

  @Bean({"RedisTemplateObjectObject"})
  public RedisTemplate<Object, Object> redisTemplateObjectObject(RedisConnectionFactory cnf) {
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(cnf);
    return redisTemplate;
  } // Tạo một RedisTemplate với kiểu dữ liệu là Object cho cả key và value, không phải là chính (Primary) để sử dụng trong toàn bộ ứng dụng
  // Khi sử dụng, thêm @Qualifier("RedisTemplateObjectObject") private final RedisTemplate<Object, Object> redisTemplate; vào class cần sử dụng là có thể sử dụng được

  @Bean
  public RedisCacheConfiguration cacheConfiguration(RedisCacheConfigProperties configs) {
    return this.createCacheConfiguration(configs.getTimeoutSeconds());
  } // Tạo cấu hình cache Redis với thời gian sống được lấy từ thuộc tính cấu hình

  @Bean
  public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfigProperties configs) {
    redisConnectionFactory.getConnection(); // Kiểm tra kết nối đến Redis
    log.info("Redis check connection success");
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>(); // Tạo một Map để lưu trữ các cấu hình cache

    for (Map.Entry<String, Long> cacheNameAndTimeout : configs.getCacheExpirations().entrySet()) { // Duyệt qua các cấu hình cache
      cacheConfigurations.put(cacheNameAndTimeout.getKey(), this.createCacheConfiguration((Long) cacheNameAndTimeout.getValue())) ; // Thêm cấu hình cache vào Map
    }

    return RedisCacheManager.builder(redisConnectionFactory) // Tạo một CacheManager với kết nối Redis
        .cacheDefaults(this.cacheConfiguration(configs)) // Thiết lập cấu hình cache mặc định
        .withInitialCacheConfigurations(cacheConfigurations) // Thiết lập các cấu hình cache cụ thể
        .build(); // Tạo một CacheManager với các cấu hình cache đã được thiết lập
  }
}
