package com.didan.archetype.constant;

public interface ResponseStatusCodeEnum {
  ResponseStatusCode SUCCESS = ResponseStatusCode.builder().code("DAN000").httpCode(200).build();
  ResponseStatusCode BUSINESS_ERROR = ResponseStatusCode.builder().code("DAN001").httpCode(400).build();
  ResponseStatusCode VALIDATION_ERROR = ResponseStatusCode.builder().code("DAN002").httpCode(400).build();
  ResponseStatusCode INTERNAL_GENERAL_SERVER_ERROR = ResponseStatusCode.builder().code("DAN003").httpCode(500).build();
  ResponseStatusCode ERROR_BODY_CLIENT = ResponseStatusCode.builder().code("DAN004").httpCode(400).build();
  ResponseStatusCode ERROR_BODY_REQUIRED = ResponseStatusCode.builder().code("DAN005").httpCode(400).build();
}
