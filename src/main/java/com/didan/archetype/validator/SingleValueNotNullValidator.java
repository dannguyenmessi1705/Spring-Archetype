package com.didan.archetype.validator;

import com.didan.archetype.validator.annotation.SingleValueNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class SingleValueNotNullValidator implements ConstraintValidator<SingleValueNotNull, Object> {

  String[] fields;

  public SingleValueNotNullValidator() {
  }

  public void initialize(final SingleValueNotNull combinedNotNull) {
    this.fields = combinedNotNull.value();
  }

  public boolean isValid(Object value, ConstraintValidatorContext context) {
    BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);

    for (String f : this.fields) {
      Object fieldValue = beanWrapper.getPropertyValue(f);
      if (fieldValue != null) {
        return true;
      }
    }

    return false;
  }
}