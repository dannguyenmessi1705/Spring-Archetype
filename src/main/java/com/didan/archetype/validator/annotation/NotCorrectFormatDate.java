package com.didan.archetype.validator.annotation;

import com.didan.archetype.validator.NotCorrectFormatDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE}) // Đánh dấu cho các kiểu phần tử mà annotation này có thể được áp dụng (trường, phương thức, tham số, hoặc annotation khác)
@Retention(RetentionPolicy.RUNTIME) // Đánh dấu cho annotation này sẽ tồn tại trong thời gian chạy
@Documented // Đánh dấu cho annotation này sẽ được đưa vào tài liệu
@Constraint( // Đánh dấu cho annotation này là một ràng buộc
  validatedBy = {NotCorrectFormatDateValidator.class}  // Chỉ định lớp validator sẽ được sử dụng để kiểm tra ràng buộc này
)
public @interface NotCorrectFormatDate {
  String message() default "Không đúng định dạng format date"; // Thông điệp mặc định khi ràng buộc không hợp lệ

  String value() default ""; // Giá trị mặc định cho ràng buộc này

  String[] values() default {}; // Danh sách các giá trị mặc định cho ràng buộc này

  Class<?>[] groups() default {}; // Nhóm mà ràng buộc này thuộc về

  Class<? extends Payload>[] payload() default {}; // Thông tin bổ sung có thể được sử dụng để truyền tải thông tin bổ sung về ràng buộc này
}

/**
Để sử dụng annotation này, bạn có thể áp dụng nó cho các trường trong lớp DTO hoặc Entity của bạn như sau:
@Valid
@NotCorrectFormatDate(message = "Ngày không đúng định dạng", value = "yyyy-MM-dd")
private String dateField;
Hoặc bạn có thể áp dụng nó cho các tham số trong phương thức như sau:
@Valid
@GetMapping("/example")
public ResponseEntity<?> example(@NotCorrectFormatDate(message = "Ngày không đúng định dạng", value = "yyyy-MM-dd") String date) {
    // Xử lý logic
    return ResponseEntity.ok("Ngày hợp lệ");
}
 */