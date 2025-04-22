package com.didan.archetype.aop;

import com.didan.archetype.aop.annotation.TimeTraceAspect;
import com.didan.archetype.utils.UtilsCommon;
import de.vandermeer.asciitable.AsciiTable;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Aspect // Đánh dấu lớp này là một Aspect trong AOP
@Configuration // Đánh dấu lớp này là một cấu hình Spring
@ConditionalOnProperty(
    value = {"app.time-trace-enabled"},
    havingValue = "true"
) // Chỉ định rằng cấu hình này sẽ được kích hoạt nếu thuộc tính "app.time-trace-enabled" có giá trị là "true"
@Slf4j
public class TimeTraceAOP {

  @Pointcut("@annotation(timeTraceAspect)") // Đánh dấu điểm cắt cho các phương thức có annotation @TimeTraceAspect
  public void pointcutAnnotationTimeTrace(TimeTraceAspect timeTraceAspect) {
  } // Phương thức này không làm gì cả, chỉ để đánh dấu điểm cắt

  @Around("pointcutAnnotationTimeTrace(timeTraceAspect)") // Xác định hành động sẽ thực hiện quanh điểm cắt
  public Object aroundProcessAnnotation(ProceedingJoinPoint joinPoint, TimeTraceAspect timeTraceAspect) throws Throwable {
    long start = System.nanoTime(); // Lưu thời gian bắt đầu
    AsciiTable at = new AsciiTable(); // Tạo bảng ASCII lưu trữ thông tin
    if (timeTraceAspect.enable()) {
      this.addRow(at, "Name", "Value");
      this.addRow(at, "Class", joinPoint.getSignature().getDeclaringTypeName());
      this.addRow(at, "MethodName", joinPoint.getSignature().getName());
      this.addRow(at, "StartTime", UtilsCommon.formatDateLog(new Date()));
    }
    Object result = joinPoint.proceed(); // Thực hiện phương thức
    if(timeTraceAspect.enable()) { // Kiểm tra xem có cần ghi log không
      this.addRow(at, "EndTime", UtilsCommon.formatDateLog(new Date()));
      this.addRow(at, "ExecutionTime", System.nanoTime() - start);
      at.addRule();
      log.info("\n{}", at.render().trim()); // Ghi log thông tin
    }
    return result; // Trả về kết quả của phương thức
  }

  private void addRow(AsciiTable at, Object... columns) {
    at.addRule();
    at.addRow(columns);
  }

}
