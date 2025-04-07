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
    prefix = "spring.kafka.producer" // Đánh dấu lớp này để ánh xạ các thuộc tính từ file cấu hình có prefix là "spring.kafka.producer"
)
@Configuration // Đánh dấu lớp này là một cấu hình Spring
@Primary // Đánh dấu lớp này là lớp chính trong Spring, sẽ được sử dụng thay cho lớp khác có cùng kiểu
 // Đánh dấu lớp này có thể được làm mới lại khi có thay đổi trong cấu hình
@ConditionalOnProperty(
    value = {"spring.kafka.enabled", "spring.kafka.producer.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaConfigProducer extends KafkaProperties.Producer {
  private boolean enabled; // Biến này xác định xem producer có được kích hoạt hay không
  private String defaultTopic; // Tên topic mặc định
  private int deliveryTimeout = 5000; // Thời gian timeout mặc định cho việc gửi tin nhắn
  private int requestTimeout = 3000; // Thời gian timeout mặc định cho yêu cầu gửi tin nhắn
  private int queueSize = 100; // Kích thước hàng đợi mặc định
}
