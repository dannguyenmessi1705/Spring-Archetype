package com.didan.archetype.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DiDanApplicationConfig {
  private Environment env;

  @Bean
  public CharacterEncodingFilter characterEncodingFilter() { // Cấu hình bộ mã hóa ký tự cho ứng dụng
    CharacterEncodingFilter filter = new CharacterEncodingFilter(); // Tạo một CharacterEncodingFilter
    filter.setEncoding("UTF-8"); // Thiết lập bộ mã hóa ký tự là UTF-8
    filter.setForceEncoding(true); // Bắt buộc sử dụng bộ mã hóa này cho tất cả các yêu cầu
    return filter; // Trả về filter đã cấu hình
  }

  public String getProperty(String key) { // Phương thức để lấy giá trị của một thuộc tính từ file cấu hình
    return env.getProperty(key); // Trả về giá trị của thuộc tính với key tương ứng
  }
}
