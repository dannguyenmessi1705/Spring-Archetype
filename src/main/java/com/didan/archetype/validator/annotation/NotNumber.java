package com.didan.archetype.validator.annotation;

import com.didan.archetype.validator.NotNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE}) // Đánh dấu cho các thành phần như trường, phương thức, tham số và chú thích
@Retention(RetentionPolicy.RUNTIME) // Chú thích này sẽ tồn tại trong thời gian chạy
@Documented // Chú thích này sẽ được ghi lại trong tài liệu
@Constraint(
  validatedBy = {NotNumberValidator.class} // Chỉ định lớp xác thực sẽ được sử dụng để kiểm tra chú thích này
)
public @interface NotNumber {
  String message() default "Không phải number"; // Thông điệp mặc định khi xác thực không thành công

  Class<?>[] groups() default {}; // Nhóm mà chú thích này thuộc về, có thể được sử dụng để phân loại các ràng buộc xác thực

  Class<? extends Payload>[] payload() default {}; // Thông tin bổ sung có thể được sử dụng bởi các công cụ xử lý chú thích

  @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE}) // Đánh dấu cho các thành phần như trường, phương thức, tham số và chú thích
  @Retention(RetentionPolicy.RUNTIME) // Chú thích này sẽ tồn tại trong thời gian chạy
  @Documented // Chú thích này sẽ được ghi lại trong tài liệu
  public @interface List {
    String[] value(); // Danh sách các giá trị của chú thích NotNumber
  }
}

/*
Ví dụ sử dụng:
@NotNumber(message = "Giá trị không phải là số")
private String value; // Trường này sẽ được xác thực không phải là số
@NotNumber.List({
  @NotNumber(message = "Giá trị không phải là số"),
  @NotNumber(message = "Giá trị không phải là số")
})
private String[] values; // Trường này sẽ được xác thực không phải là số
 */