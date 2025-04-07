package com.didan.archetype.config.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConfigurationProperties(
    prefix = "spring.kafka" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "spring.kafka"
)
@Configuration
@Primary // Đánh dấu lớp này là lớp chính trong Spring, sẽ được sử dụng thay cho lớp khác có cùng kiểu
@RefreshScope
@ConditionalOnProperty(
    value = {"spring.kafka.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaConfigProperties extends KafkaProperties {
  private boolean enabled;
}
