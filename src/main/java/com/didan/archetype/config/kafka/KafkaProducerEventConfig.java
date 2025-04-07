package com.didan.archetype.config.kafka;

import com.didan.archetype.config.properties.KafkaConfigProducer;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@ConditionalOnProperty(
    value = {"spring.kafka.enabled", "spring.kafka.producer.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@RequiredArgsConstructor
@Data
@Slf4j
public class KafkaProducerEventConfig {
  final KafkaConfigProducer kafkaConfigProducer;

  @Value("${spring.kafka.auth.username:#{null}}")
  private String authUsername; // Tên người dùng để xác thực (nếu có)

  @Value("${spring.kafka.auth.password:#{null}}")
  private String authPassword; // Mật khẩu để xác thực (nếu có)

  @Bean({"kafkaEventProducerFactory"})
  public ProducerFactory<String, Object> kafkaEventProducerFactory() {
    Map<String, Object> props = new HashMap<>(); // Tạo một Map để chứa các thuộc tính cấu hình cho producer
    props.put("bootstrap.servers", kafkaConfigProducer.getBootstrapServers()); // Đặt server Kafka
    props.put("key.serializer", StringSerializer.class); // Đặt serializer cho key có kiểu String
    props.put("value.serializer", JsonSerializer.class); // Đặt serializer cho value có kiểu Json
    props.put("retries", this.kafkaConfigProducer.getRetries()); // Số lần thử gửi lại tin nhắn
    props.put("delivery.timeout.ms", this.kafkaConfigProducer.getDeliveryTimeout()); // Thời gian timeout cho việc gửi tin nhắn
    props.put("request.timeout.ms", this.kafkaConfigProducer.getRequestTimeout()); // Thời gian timeout cho yêu cầu gửi tin nhắn
    addAuth(props);
    return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer()); // Tạo một DefaultKafkaProducerFactory với các thuộc tính đã cấu hình
  }

  private void addAuth(Map<String, Object> props) {
    if (this.authUsername != null && this.authPassword != null) {
      props.put("sasl.jaas.config",
          String.format("%s required username=\"%s\" password=\"%s\";", PlainLoginModule.class.getName(), this.authUsername, this.authPassword)); // Thêm cấu hình xác thực SASL
      props.put("sasl.mechanism", "PLAIN"); // Đặt cơ chế xác thực là PLAIN
      props.put("security.protocol", "SASL_PLAINTEXT"); // Đặt giao thức bảo mật là SASL_PLAINTEXT
    }
  }

  @Bean({"kafkaEventTemplate"})
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(kafkaEventProducerFactory());
  }
}
