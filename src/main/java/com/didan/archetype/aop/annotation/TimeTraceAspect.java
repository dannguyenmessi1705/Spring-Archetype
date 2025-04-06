package com.didan.archetype.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD}) // Đánh dấu @TimeTraceAspect lên phương thức
@Retention(RetentionPolicy.RUNTIME) // Đánh dấu @TimeTraceAspect có thể được sử dụng tại runtime
public @interface TimeTraceAspect { // Đánh dấu @TimeTraceAspect lên phương thức
  boolean enable() default true; // Biến enable để bật/tắt việc ghi lại thời gian thực thi
}

// Sử dụng @TimeTraceAspect trong các phương thức mà bạn muốn ghi lại thời gian thực thi
// Ví dụ:
// @TimeTraceAspect
// public void someMethod() {
//     // Thực hiện một số công việc
// }
// ==> Khi phương thức được gọi, thời gian thực thi sẽ được ghi lại và in ra console.
