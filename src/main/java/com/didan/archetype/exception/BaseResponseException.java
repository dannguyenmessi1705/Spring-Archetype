package com.didan.archetype.exception;

import com.didan.archetype.constant.ResponseStatusCode;
import com.didan.archetype.factory.response.GeneralResponse;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseResponseException extends RuntimeException {
  private final ResponseStatusCode responseStatusCode;
  private GeneralResponse<Object> generalResponse;
  private Map<String, String> params;

  public BaseResponseException(ResponseStatusCode responseStatusCode) {
    this.responseStatusCode = responseStatusCode;
  }

  public BaseResponseException(GeneralResponse<Object> generalResponse, ResponseStatusCode responseStatusCode) {
    this.generalResponse = generalResponse;
    this.responseStatusCode = responseStatusCode;
  }

  public BaseResponseException(GeneralResponse<Object> generalResponse, ResponseStatusCode responseStatusCode, Map<String, String> params) {
    this.generalResponse = generalResponse;
    this.responseStatusCode = responseStatusCode;
    this.params = params;
  }
}
