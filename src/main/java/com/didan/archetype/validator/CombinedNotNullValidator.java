package com.didan.archetype.validator;

import com.didan.archetype.validator.annotation.CombinedNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class CombinedNotNullValidator implements ConstraintValidator<CombinedNotNull, Object> {
  String[] fields;

  public CombinedNotNullValidator() {
  }

  public void initialize(final CombinedNotNull combinedNotNull) {
    this.fields = combinedNotNull.value();
  } // Hàm này dùng để khởi tạo các trường cần kiểm tra không null từ annotation CombinedNotNull

  public boolean isValid(Object value, ConstraintValidatorContext context) { // Hàm này dùng để kiểm tra các trường không null
    BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);

    for(String f : this.fields) {
      Object fieldValue = beanWrapper.getPropertyValue(f); // Lấy giá trị của trường từ đối tượng value
      if (fieldValue == null) { // Nếu trường có giá trị null
        return false; // Trả về false
      }
    }
    return true;
  }
}