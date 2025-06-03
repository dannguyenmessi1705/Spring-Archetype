package com.didan.archetype.config;

import com.didan.archetype.config.properties.SwaggerConfigProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OpenApiConfig.SwaggerAuthProperties.class})
public class OpenApiConfig {

  private final SwaggerConfigProperties swaggerConfigProperties;
  private final SwaggerAuthProperties swaggerAuthProperties;

  public OpenApiConfig(SwaggerConfigProperties swaggerConfigProperties,
      SwaggerAuthProperties swaggerAuthProperties) {
    this.swaggerConfigProperties = swaggerConfigProperties;
    this.swaggerAuthProperties = swaggerAuthProperties;
  }

  @Bean
  public OpenAPI customOpenAPI() {
    OpenAPI openAPI = new OpenAPI()
        .components(new Components()) // Initialize components
        .info(new Info()
            .title(swaggerConfigProperties.getTitle())
            .description(swaggerConfigProperties.getDescription())
            .contact(new Contact()
                .name(swaggerConfigProperties.getContactName())
                .url(swaggerConfigProperties.getContactUrl()))
            .version(swaggerConfigProperties.getVersion()));

    // Add security configuration if enabled
    if (swaggerAuthProperties.isEnabled()) {
      addSecurityToOpenAPI(openAPI);
    }

    return openAPI;
  }

  private void addSecurityToOpenAPI(OpenAPI openAPI) {
    SecurityScheme securityScheme = new SecurityScheme()
        .name(swaggerAuthProperties.getName())
        .type(swaggerAuthProperties.getType())
        .bearerFormat(swaggerAuthProperties.getBearerFormat())
        .scheme(swaggerAuthProperties.getScheme())
        .description(swaggerAuthProperties.getDescription())
        .in(swaggerAuthProperties.getIn());

    // Correct method to add security scheme to OpenAPI
    openAPI.getComponents().addSecuritySchemes(swaggerAuthProperties.getName(), securityScheme);

    // Add security requirement globally to all operations
    openAPI.addSecurityItem(new SecurityRequirement()
        .addList(swaggerAuthProperties.getName()));
  }

  @ConfigurationProperties(prefix = "swagger.auth")
  @Data
  public static class SwaggerAuthProperties {
    private boolean enabled = false;
    private String name = "bearerAuth";
    private SecurityScheme.Type type = Type.HTTP;
    private String bearerFormat = "JWT";
    private String scheme = "bearer";
    private String description = "Header Authorization : token Bearer";
    private SecurityScheme.In in = In.HEADER;
  }
}