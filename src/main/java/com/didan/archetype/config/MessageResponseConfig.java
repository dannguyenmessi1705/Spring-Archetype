package com.didan.archetype.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
    prefix = "message-response" // Tiền tố cho các thuộc tính trong file cấu hình
)
@Data
public class MessageResponseConfig {
  private Map<String, String> params;
}
