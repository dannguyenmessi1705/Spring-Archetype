package com.didan.archetype.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements ApplicationContextAware {
  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext context) {
    applicationContext = context;
  }

  public static Object getBean(String name) { // Hàm này dùng để lấy bean từ ApplicationContext
    return applicationContext.getBean(name);
  }
}
