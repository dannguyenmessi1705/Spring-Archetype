package com.didan.archetype.utils;

import com.didan.archetype.constant.ResponseStatusCode;
import com.didan.archetype.factory.response.GeneralResponse;
import com.didan.archetype.factory.response.ResponseStatus;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingResponseWrapper;

@UtilityClass
@Slf4j
public class UtilsCommon {

  public static final Gson GSON = (new GsonBuilder()).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create(); // Tao một đối tượng Gson với định dạng ngày tháng cụ thể
  public static final List<String> ignorePatterns = new ArrayList<>(); // Danh sách các mẫu bỏ qua

  static {
    ignorePatterns.add("(?<=\"password\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"pin\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"token\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"loginToken\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"accessToken\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"sessionToken\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"refreshToken\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"passwordEx\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"newPin\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"currentPin\":\")[^\"]+(?=\")");
    ignorePatterns.add("(?<=\"confirmPin\":\")[^\"]+(?=\")");
  }

  public static String createNewUUID() {
    return UUID.randomUUID().toString().replace("-", "").toLowerCase(); // Tạo một UUID mới và loại bỏ dấu "-"
  } // Phương thức này sẽ tạo một UUID mới và loại bỏ dấu "-"

  public static String formatDateLog(Date date) {
    String dateStr = formatDateWithPattern(date, "yyyy-MM-dd - HH:mm:ss Z");
    return dateStr.replace("0700", "07:00"); // Thay đinh định dạng thời gian
  } // Phương thức này sẽ định dạng ngày tháng theo mẫu đã chỉ định

  public static String formatDateWithPattern(Date date, String patternFormatDate) {
    SimpleDateFormat format = new SimpleDateFormat(patternFormatDate);
    return format.format(date);
  } // Phương thức này sẽ định dạng ngày tháng theo mẫu đã chỉ định

  public static String hideSensitiveData(String data) {
    data = data.replaceAll("\\\\n", "").replaceAll("\\\\r", "").replaceAll("\\\\", "").replaceAll("\\s*\"(\\w+)\"\\s*:\\s*\"(.*?)\"\\s*", "\"$1\":\"$2\"");

    for (String ignorePattern : ignorePatterns) {
      data = data.replaceAll(ignorePattern, "*");
    }

    return data;
  } // Phương thức này sẽ thay thế các mẫu nhạy cảm trong dữ liệu bằng dấu "*"

  public static boolean isIgnoredUri(String uri) {
    return uri.contains("swagger")
        || uri.contains("actuator")
        || uri.contains("api-docs");
  }


  public static boolean isMultipart(HttpServletRequest request) {
    return request.getContentType() != null && request.getContentType().startsWith("multipart/");
  }

  public static void sendError(ContentCachingResponseWrapper responseWrapper, ResponseStatusCode errorCode) {
    try {
      responseWrapper.setStatus(errorCode.getHttpCode());
      responseWrapper.setContentType("application/json; charset=UTF-8");
      GeneralResponse<Object> responseObject = new GeneralResponse<>();
      ResponseStatus responseStatus = new ResponseStatus(errorCode.getCode(), true);
      responseStatus.setResponseTime(new Date());
      responseObject.setStatus(responseStatus);
      new ObjectMapper()
          .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
          .registerModule(new JavaTimeModule())
          .setDateFormat(new StdDateFormat())
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .setTimeZone(Calendar.getInstance().getTimeZone())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .writeValue(responseWrapper.getWriter(), responseObject);
      responseWrapper.copyBodyToResponse();
    } catch (IOException e) {
      log.error("io exception when send error response: {}", e.getMessage(), e);
    }
  }

}
