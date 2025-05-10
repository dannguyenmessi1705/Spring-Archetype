package com.didan.archetype.locale;

import com.didan.archetype.config.properties.AppConfigProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

  final AppConfigProperties appConfigProperties;
  private final List<Locale> locales = new ArrayList<>();
  private final Locale defaultLanguage;

  public CustomLocaleResolver(AppConfigProperties appConfigProperties) {
    this.appConfigProperties = appConfigProperties;
    this.appConfigProperties.getLocaleResolverLanguages().forEach(l -> this.locales.add(Locale.forLanguageTag(l)));
    this.defaultLanguage = Locale.forLanguageTag(appConfigProperties.getDefaultLanguage());
  }

  @NotNull
  @Override
  public Locale resolveLocale(HttpServletRequest request) { // Phương thức này được gọi để xác định ngôn ngữ dựa trên header "Accept-Language" trong request
    String headerLang = request.getHeader("Accept-Language"); // Lấy giá trị của header "Accept-Language"
    return headerLang != null && !headerLang.isEmpty() ? Locale.lookup(LanguageRange.parse(headerLang), this.locales) : this.defaultLanguage; // Nếu header không null và không rỗng, tìm ngôn ngữ phù hợp trong danh sách locales, nếu không thì sử dụng ngôn ngữ mặc định
  }

  @Bean
  @Primary // Đánh dấu bean này là chính, sẽ được sử dụng thay thế cho các bean khác cùng loại
  public LocaleResolver localeResolver() { // Phương thức này tạo ra một bean LocaleResolver
    return this; // Trả về chính đối tượng này
  }

  @Bean // Đánh dấu phương thức này là một bean trong Spring
  public ResourceBundleMessageSource messageSource() { // Phương thức này tạo ra một bean ResourceBundleMessageSource
    ResourceBundleMessageSource rs = new ResourceBundleMessageSource(); // Tạo một đối tượng ResourceBundleMessageSource
    rs.setBasename("messages"); // Thiết lập tên file properties là "messages"
    rs.setDefaultEncoding("UTF-8"); // Thiết lập bộ mã hóa là UTF-8
    rs.setUseCodeAsDefaultMessage(true); // Sử dụng mã lỗi làm thông điệp mặc định nếu không tìm thấy thông điệp tương ứng
    return rs;
  }
}
