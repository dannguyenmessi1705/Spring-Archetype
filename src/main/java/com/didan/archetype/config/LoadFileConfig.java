package com.didan.archetype.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:config.properties"}) // Đọc file cấu hình config.properties (thay vì dùng application.properties, tránh xung đột)
public class LoadFileConfig {
  public LoadFileConfig() {}
}
