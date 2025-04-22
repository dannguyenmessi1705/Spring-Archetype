package com.didan.archetype.validator.annotation;

import com.didan.archetype.validator.CombinedNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Đánh dấu annotation này sẽ có hiệu lực tại runtime
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) // Đánh dấu annotation này có thể được áp dụng cho class hoặc annotation khác
@Constraint(
  validatedBy = {CombinedNotNullValidator.class} // Chỉ định validator sẽ được sử dụng để kiểm tra annotation này
)
public @interface CombinedNotNull {
  String message() default "file1 and file2... is required"; // Thông điệp mặc định khi validation không thành công

  Class<?>[] groups() default {}; // Nhóm mà annotation này thuộc về, có thể sử dụng để phân loại các validation khác nhau

  Class<? extends Payload>[] payload() default {}; // Thông tin bổ sung có thể được sử dụng bởi các framework khác nhau

  String[] value() default {}; // Các trường cần kiểm tra, có thể là tên của các trường trong class được áp dụng annotation này
}

/**
Để sử dụng trong DTO, bạn cần tạo một class DTO và áp dụng annotation này lên class đó. Ví dụ:
package com.didan.archetype.dto;
import com.didan.archetype.validator.annotation.CombinedNotNull;
class MyDTO {
  @CombinedNotNull(value = {"field1", "field2"}, message = "field1 and field2 are required")
  private String field1;
  private String field2;
*/