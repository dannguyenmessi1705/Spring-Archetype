package com.didan.archetype.validator.annotation;

import com.didan.archetype.validator.SingleValueNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Giữ lại annotation trong thời gian chạy
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) // Áp dụng cho lớp và annotation
@Constraint(
  validatedBy = {SingleValueNotNullValidator.class}
)
public @interface SingleValueNotNull {
  String message() default "file1 or file2 is required"; // Thay đổi thông báo lỗi mặc định

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] value() default {};
}

/*
Ví dụ sử dụng:
@SingleValueNotNull(value = {"file1", "file2"}, message = "file1 or file2 is required")
public class MyClass {
    @SingleValueNotNull(value = {"file1", "file2"}, message = "file1 or file2 is required")
    private String file1;
    private String file2;

    // getters and setters
}
 */
