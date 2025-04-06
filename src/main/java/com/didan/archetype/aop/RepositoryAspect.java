package com.didan.archetype.aop;

import com.didan.archetype.config.properties.AppConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Configuration // Đánh dấu lớp này là một cấu hình Spring
@Aspect // Đánh dấu lớp này là một Aspect trong AOP
@Slf4j
@RequiredArgsConstructor
public class RepositoryAspect {

  private final AppConfigProperties appConfigProperties;

  @Around("execution(* com.didan.*.repository.*.*(..))") // Chỉ định các phương thức trong package repository
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis(); // Lưu thời gian bắt đầu
    Object proceed = joinPoint.proceed(); // Thực hiện phương thức
    long executionTime = System.currentTimeMillis() - start; // Tính thời gian thực hiện
    String message = joinPoint.getSignature() + " exec in " + executionTime + "ms"; // Tạo thông điệp log
    if (executionTime > appConfigProperties.getRepositoryQueryLimitWarningMs()) {
      log.warn(appConfigProperties.getApplicationShortName() + " : " + message + " : SLOW QUERY"); // Ghi log cảnh báo nếu thời gian thực hiện lớn hơn ngưỡng
    }
    return proceed; // Trả về kết quả của phương thức
  }
}
