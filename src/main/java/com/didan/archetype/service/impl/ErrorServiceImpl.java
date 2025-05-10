package com.didan.archetype.service.impl;

import com.didan.archetype.locale.Translator;
import com.didan.archetype.service.ErrorService;
import org.springframework.stereotype.Service;

@Service
public class ErrorServiceImpl implements ErrorService {

  @Override
  public String getErrorDetail(String errorCode, String language) {
    return Translator.toLocale(errorCode, language);
  }
}
