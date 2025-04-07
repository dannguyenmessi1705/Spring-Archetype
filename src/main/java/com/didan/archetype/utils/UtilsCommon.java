package com.didan.archetype.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@UtilityClass
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

}
