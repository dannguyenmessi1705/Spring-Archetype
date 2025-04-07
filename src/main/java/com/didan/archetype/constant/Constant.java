package com.didan.archetype.constant;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class Constant {
  public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // Định dạng ngày giờ theo chuẩn ISO 8601
  public static final String PHONE_REGEX = "(84+([0-9]{9})\\b)"; // Biểu thức chính quy để kiểm tra định dạng số điện thoại
  public static final String CHECK_NUMBER_REGEX = "[0-9]+"; // Biểu thức chính quy để kiểm tra xem chuỗi có phải là số hay không
  public static final String LANGUAGE_VI = "vi"; // Mã ngôn ngữ tiếng Việt
  public static final String SMS_TIME = "dd/MM/yyyy HH:mm:ss"; // Định dạng ngày giờ cho SMS
  public static final String ALERT_PUSH_TIME = "yyyyMMddHHmm"; // Định dạng ngày giờ cho thông báo đẩy

  public static Date formatDate(final String date, final String pattern) {
    try {
      SimpleDateFormat format = new SimpleDateFormat(pattern);
      return format.parse(date);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  } // Phương thức để định dạng ngày giờ từ chuỗi theo định dạng đã cho

}
