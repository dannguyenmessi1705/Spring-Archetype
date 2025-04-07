package com.didan.archetype.config.kafka;

import com.didan.archetype.config.properties.KafkaConfigConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration // Đánh dấu lớp này là một lớp cấu hình trong Spring
@EnableKafka // Kích hoạt Kafka trong ứng dụng Spring
@ConditionalOnProperty(
    value = {"spring.kafka.enabled", "spring.kafka.consumer.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
@Slf4j
@Data
@RequiredArgsConstructor
public class KafkaConsumerConfig {

  final KafkaConfigConsumer kafkaConfigConsumer;

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers; // Server Kafka để kết nối

  @Value("${spring.kafka.auth.username:#{null}}")
  private String authUsername; // Tên người dùng để xác thực (nếu có)

  @Value("${spring.kafka.auth.password:#{null}}")
  private String authPassword; // Mật khẩu để xác thực (nếu có)

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() { // Tạo một ConsumerFactory cho Kafka
    Map<String, Object> props = new HashMap<>(); // Tạo một Map để chứa các thuộc tính cấu hình cho consumer
    props.put("bootstrap.servers", bootstrapServers); // Đặt server Kafka
    if (Objects.nonNull(kafkaConfigConsumer.getGroupId())) { // Nếu có groupId, thêm vào cấu hình
      props.put("group.id", kafkaConfigConsumer.getGroupId());
    } else {
      props.put("group.id", "default"); // Nếu không có groupId, sử dụng giá trị mặc định
    }
    props.put("key.deserializer", StringDeserializer.class); // Đặt deserializer cho key có kiểu String
    props.put("value.deserializer", StringDeserializer.class); // Đặt deserializer cho value có kiểu String
    this.addAuth(props);
    return new DefaultKafkaConsumerFactory<>(props); // Tạo một DefaultKafkaConsumerFactory với các thuộc tính đã cấu hình
  }

  private void addAuth(Map<String, Object> props) {
    if (this.authUsername != null && this.authPassword != null) {
      props.put("sasl.jaas.config",
          String.format("%s required username=\"%s\" password=\"%s\";", PlainLoginModule.class.getName(), this.authUsername, this.authPassword)); // Thêm cấu hình xác thực SASL
      props.put("sasl.mechanism", "PLAIN"); // Đặt cơ chế xác thực là PLAIN
      props.put("security.protocol", "SASL_PLAINTEXT"); // Đặt giao thức bảo mật là SASL_PLAINTEXT
    }
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() { // Tạo một ConcurrentKafkaListenerContainerFactory cho Kafka listener
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory()); // Thiết lập ConsumerFactory cho factory
    return factory;
  }

}
