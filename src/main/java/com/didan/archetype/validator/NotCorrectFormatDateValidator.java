package com.didan.archetype.validator;

import com.didan.archetype.constant.Constant;
import com.didan.archetype.validator.annotation.NotCorrectFormatDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class NotCorrectFormatDateValidator implements ConstraintValidator<NotCorrectFormatDate, String> {
  NotCorrectFormatDate notCorrectFormatDate;

  @Override
  public void initialize(NotCorrectFormatDate constraintAnnotation) {
    this.notCorrectFormatDate = constraintAnnotation;
  }

  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // Không kiểm tra null
    } else {
      if (!StringUtils.isEmpty(this.notCorrectFormatDate.value())) { // Nếu có định dạng cụ thể được chỉ định
        Date date = Constant.formatDate(value, this.notCorrectFormatDate.value()); // Kiểm tra định dạng cụ thể
        if (date != null) { // Nếu định dạng cụ thể hợp lệ
          return true;
        }
      }

      for(int i = 0; i < this.notCorrectFormatDate.values().length; ++i) { // Nếu không có định dạng cụ thể, kiểm tra tất cả các định dạng được chỉ định
        Date date = Constant.formatDate(value, this.notCorrectFormatDate.values()[i]); // Kiểm tra định dạng cụ thể
        if (date != null) { // Nếu định dạng cụ thể hợp lệ
          return true;
        }
      }

      return false;
    }
  }
}
