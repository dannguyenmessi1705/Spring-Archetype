package com.didan.archetype.config.properties;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Đánh dấu lớp này là một cấu hình Spring
public class KafkaFixOutput { // Lớp này định nghĩa một cấu hình cho Kafka output
  @Bean
  public Supplier<String> output() { // Phương thức này định nghĩa một bean có tên là "output" để cung cấp một Supplier<String>
    return () -> "¯\\_(ツ)_/¯";
  }
}
