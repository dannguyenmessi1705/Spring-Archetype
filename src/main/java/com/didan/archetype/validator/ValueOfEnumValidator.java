package com.didan.archetype.validator;

import com.didan.archetype.validator.annotation.ValueOfEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
  private List<String> acceptedValues;

  @Override
  public void initialize(ValueOfEnum annotation) {
    this.acceptedValues = Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name).collect(Collectors.toList());
  }

  public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
    return value == null || this.acceptedValues.contains(value.toString());
  }
}
