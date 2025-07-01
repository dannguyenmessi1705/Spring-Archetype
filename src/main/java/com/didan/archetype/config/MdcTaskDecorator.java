package com.didan.archetype.config;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

/**
 * @author dannd1
 * @since 7/1/2025
 */
public class MdcTaskDecorator implements TaskDecorator {

  @NotNull
  @Override
  public Runnable decorate(@NotNull Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      if (contextMap != null) {
        MDC.setContextMap(contextMap);
      }
      try {
        runnable.run();
      } finally {
        MDC.clear();
      }
    };
  }
}
