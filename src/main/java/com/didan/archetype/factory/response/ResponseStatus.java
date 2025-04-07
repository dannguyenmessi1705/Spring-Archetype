package com.didan.archetype.factory.response;

import com.didan.archetype.locale.Translator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ResponseStatus {
  @JsonProperty("code")
  private String code;
  @JsonProperty("message")
  private String message;
  @JsonProperty("responseTime")
  @JsonFormat(
      shape = Shape.STRING, // Định dạng kiểu chuỗi
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" // Định dạng ngày giờ theo chuẩn ISO 8601
  ) // Định dạng ngày giờ theo chuẩn ISO 8601
  private Date responseTime;
  @JsonProperty("displayMessage")
  private String displayMessage; // Thông điệp hiển thị cho người dùng

  public ResponseStatus(String code, boolean setMessageImplicity) {
    setCode(code, setMessageImplicity); // Gọi phương thức setCode để thiết lập mã và thông điệp
  }

  public void setCode(String code, boolean setMessageImplicity) {
    this.code = code;
    if (setMessageImplicity) {
      this.message = Translator.toLocale(code); // Lấy thông điệp từ Translator dựa trên mã code
    }
    this.displayMessage = this.message;
  }

  public ResponseStatus(final String code, final String message, final Date responseTime, final String displayMessage) {
    this.code = code; // Mã trạng thái
    this.message = message; // Thông điệp trạng thái
    this.responseTime = responseTime; // Thời gian phản hồi
    this.displayMessage = displayMessage; // Thông điệp hiển thị cho người dùng
  }
}
