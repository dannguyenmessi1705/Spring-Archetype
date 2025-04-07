package com.didan.archetype.locale;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class Translator {
  private static ResourceBundleMessageSource messageSource;

  @Autowired
  Translator(ResourceBundleMessageSource messageSource) {
    Translator.messageSource = messageSource;
  }

  public static String toLocale(String messageCode, Object... args) { // Hàm này dùng để lấy thông điệp từ file properties dựa trên mã thông điệp, parameter 2 là các tham số thay thế
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, args, locale);
  }
}
