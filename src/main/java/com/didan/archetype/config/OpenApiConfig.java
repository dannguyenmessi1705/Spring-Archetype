package com.didan.archetype.config;

import com.didan.archetype.config.properties.SwaggerConfigProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  final SwaggerConfigProperties swaggerConfigProperties;

  public OpenApiConfig(SwaggerConfigProperties swaggerConfigProperties) {
    this.swaggerConfigProperties = swaggerConfigProperties;
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return (new OpenAPI())
        .info(new Info()
            .title(swaggerConfigProperties.getTitle())
            .description(swaggerConfigProperties.getDescription())
            .contact((new Contact())
                .name(swaggerConfigProperties.getContactName())
                .url(swaggerConfigProperties.getContactUrl()))
            .version(swaggerConfigProperties.getVersion()));
  }

  @Configuration
  @ConditionalOnProperty(
      value = {"swagger.auth.enabled"},
      havingValue = "true" // Nếu thuộc tính swagger.auth.enabled có giá trị true thì mới kích hoạt cấu hình này
  )
  @ConfigurationProperties(
      prefix = "swagger.auth" // Tiền tố cho các thuộc tính trong file cấu hình
  )
  @Data
  public static class ConfigAuthOpenApi { // Cấu hình bảo mật cho OpenAPI
    private final OpenAPI openAPI; // Đối tượng OpenAPI để cấu hình
    private boolean enabled; // Biến này có thể được sử dụng để kiểm tra xem cấu hình bảo mật có được kích hoạt hay không
    private String name; // Tên của bảo mật
    private SecurityScheme.Type type; // Kiểu bảo mật (HTTP, API Key, OAuth2, ...)
    private String bearerFormat; // Định dạng của bearer token
    private String scheme; // Kiểu bảo mật (Bearer, Basic, ...)
    private String description; // Mô tả về bảo mật
    private SecurityScheme.In in; // Vị trí của bảo mật (Header, Query, Cookie)

    public ConfigAuthOpenApi(OpenAPI openAPI) {
      this.openAPI = openAPI;
      this.type = Type.HTTP;
      this.bearerFormat = "bearer";
      this.description = "Header Authorization : token Bearer";
      this.in = In.HEADER;
    }

    @Bean
    public void addSecurity() {
      SecurityScheme securityScheme = new SecurityScheme(); // Tạo một đối tượng SecurityScheme để cấu hình bảo mật
      securityScheme.setName(this.name); // Tên của bảo mật
      securityScheme.setType(Type.HTTP); // Kiểu bảo mật (HTTP)
      securityScheme.setBearerFormat(this.bearerFormat); // Định dạng của bearer token
      securityScheme.setScheme(this.scheme); // Kiểu bảo mật (Bearer, Basic, ...)
      securityScheme.setDescription(this.description); // Mô tả về bảo mật
      securityScheme.setIn(this.in); // Vị trí của bảo mật (Header, Query, Cookie)
      this.openAPI.schemaRequirement(this.name, securityScheme); // Thêm bảo mật vào OpenAPI
    }
  }
}
