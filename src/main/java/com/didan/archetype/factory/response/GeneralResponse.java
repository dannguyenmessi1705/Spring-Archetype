package com.didan.archetype.factory.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponse<T> {
  @JsonProperty("status") // Đặt tên trường là "status" trong JSON
  private ResponseStatus status;
  @JsonProperty("data") // Đặt tên trường là "data" trong JSON
  private T data;
}
