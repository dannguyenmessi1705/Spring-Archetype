package com.didan.archetype.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ServiceSelfHealth implements HealthIndicator {

  @Override
  public Health health() {
    return Health.up().withDetail("service_status", "available").build();
  }
}
