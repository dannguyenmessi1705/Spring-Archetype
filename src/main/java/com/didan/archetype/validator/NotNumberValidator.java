package com.didan.archetype.validator;

import com.didan.archetype.validator.annotation.NotNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NotNumberValidator implements ConstraintValidator<NotNumber, String> {
  NotNumber notCorrectFormatDate;

  @Override
  public void initialize(NotNumber constraintAnnotation) {
    this.notCorrectFormatDate = constraintAnnotation;
  }

  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !StringUtils.hasLength(value) || value.matches("[0-9]+");
  }
}
