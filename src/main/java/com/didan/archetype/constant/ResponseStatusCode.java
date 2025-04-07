package com.didan.archetype.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ResponseStatusCode {
  private final String code;
  private final int httpCode;
}
