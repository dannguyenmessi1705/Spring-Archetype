package com.didan.archetype.config.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(
    prefix = "spring.kafka.consumer" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "spring.kafka.consumer"
)
@Configuration
@Primary // Đánh dấu lớp này là lớp chính trong Spring, sẽ được sử dụng thay cho lớp khác có cùng kiểu
@RefreshScope // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@ConditionalOnProperty(
    value = {"spring.kafka.enabled", "spring.kafka.consumer.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
public class KafkaConfigConsumer extends KafkaProperties.Consumer {
  private boolean enabled; // Biến này xác định xem consumer có được kích hoạt hay không
}
