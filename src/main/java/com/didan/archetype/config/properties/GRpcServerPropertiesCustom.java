package com.didan.archetype.config.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties("grpc")
@Primary
@Data
public class GRpcServerPropertiesCustom extends GRpcServerProperties {

  @Value("${grpc.client-request-timeout-ms:#{10000}}")
  private Long clientRequestTimeoutMs;
  @Value("${grpc.client-request-log:#{false}}")
  private boolean clientRequestLog;
  @Value("${grpc.server-log:#{false}}")
  private boolean serverLog;
}