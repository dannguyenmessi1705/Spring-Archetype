package com.didan.archetype.config;

import com.didan.archetype.config.properties.AppConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // Kích hoạt tính năng xử lý bất đồng bộ trong Spring
@Slf4j
@RequiredArgsConstructor
public class AsyncConfig {
  private final AppConfigProperties appConfigProperties;

  @Bean({"threadPoolTaskExecutor"})
  public TaskExecutor getAsyncExecutor() {
    log.info("corePoolSize: {}", appConfigProperties.getAsyncExecutorCorePoolSize() + ", maxPoolSize: " + appConfigProperties.getAsyncExecutorMaxPoolSize());
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // Tạo một ThreadPoolTaskExecutor dùng để xử lý các tác vụ bất đồng bộ
    executor.setCorePoolSize(appConfigProperties.getAsyncExecutorCorePoolSize()); // Số lượng luồng tối thiểu trong pool
    executor.setMaxPoolSize(appConfigProperties.getAsyncExecutorMaxPoolSize()); // Số lượng luồng tối đa trong pool
    executor.setWaitForTasksToCompleteOnShutdown(true); // Đợi cho các tác vụ đang chạy hoàn thành trước khi tắt executor
    executor.setThreadNamePrefix(appConfigProperties.getAsyncExecutorThreadNamePrefix()); // Tiền tố cho tên luồng
    executor.setTaskDecorator(new MdcTaskDecorator()); // Sử dụng MdcTaskDecorator để giữ lại thông tin MDC trong các luồng bất đồng bộ
    return executor;
  }
}
