package com.didan.archetype.validator.annotation;

import com.didan.archetype.validator.ValueOfEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE}) // Đánh dấu cho các kiểu phần tử mà annotation
// này có thể được áp dụng bao gồm: phương thức, trường, kiểu annotation khác, constructor, tham số và kiểu sử dụng
@Retention(RetentionPolicy.RUNTIME) // Đánh dấu rằng annotation này sẽ được giữ lại trong thời gian chạy
@Documented // Đánh dấu rằng annotation này sẽ được đưa vào tài liệu Javadoc
@Constraint(
  validatedBy = {ValueOfEnumValidator.class} // Đánh dấu rằng annotation này sẽ được xác thực bởi lớp ValueOfEnumValidator
)
public @interface ValueOfEnum {
  Class<? extends Enum<?>> enumClass(); // Đánh dấu rằng thuộc tính này sẽ chứa lớp enum mà giá trị của trường được xác thực phải thuộc về

  String message() default "must be any of enum {enumClass}"; // Thông điệp mặc định sẽ được sử dụng nếu giá trị không hợp lệ

  Class<?>[] groups() default {}; // Nhóm mà annotation này thuộc về, có thể được sử dụng để phân loại các ràng buộc

  Class<? extends Payload>[] payload() default {}; // Thông tin bổ sung có thể được sử dụng bởi các công cụ xử lý ràng buộc
}

/**
Ví dụ:
public enum MyEnum {
    VALUE1,
    VALUE2,
    VALUE3
}
public class MyClass {
    @ValueOfEnum(enumClass = MyEnum.class, message = "Invalid value for myEnum")
    private String myEnum;
}
 */