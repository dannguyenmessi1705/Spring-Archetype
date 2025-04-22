package com.didan.archetype.config.resttemplate;

import com.didan.archetype.utils.UtilsCommon;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

@Configuration
@Slf4j
public class RestTemplateTracer {
  @Value("${app.log-request-http.ignored-headers:#{null}")
  private String ignoredHeadersConfig; // Các header cần ẩn trong log

  public void trace(HttpRequest request, byte[] body, ClientHttpResponse response, Date startDate) { // Log request và response
    List<String> ignoredHeaders = new ArrayList<>();
    if (!StringUtils.isEmpty(ignoredHeadersConfig)) { // Nếu có cấu hình các header cần ẩn
      ignoredHeadersConfig = ignoredHeadersConfig.replaceAll("\\s+", ""); // Xóa khoảng trắng
      ignoredHeaders = Arrays.asList(ignoredHeadersConfig.split(",")); // Tách các header thành danh sách
    }
    StringBuilder sb = new StringBuilder();  // StringBuilder để lưu thông tin log

    try (InputStream inputStreamBody = response.getBody(); // Lấy input stream của response
        InputStreamReader inputStreamReader = new InputStreamReader(inputStreamBody, StandardCharsets.UTF_8); // Đọc input stream
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader) // Đọc dữ liệu từ input stream
    ) {
      HttpHeaders headers = request.getHeaders(); // Lấy thông tin header của request
      HttpHeaders headersLog = hideSensitiveHeaders(headers, ignoredHeaders);
      for (String ignoredHeader : ignoredHeaders) {  // Ẩn các header cần ẩn
        headersLog.replace(ignoredHeader, Collections.singletonList("***"));
      }
      long start = startDate.getTime(); // Lấy thời gian bắt đầu gọi API
      sb.append("Request  : [").append(request.getMethod()).append("] ").append(request.getURI()).append(" \n")
          .append("Time     : ").append(UtilsCommon.formatDateLog(startDate)).append(" \n")
          .append("Headers  : ").append(headersLog).append(" \n")
          .append("Body     : ").append(new String(body, StandardCharsets.UTF_8)).append(" \n")
          .append(" \n"); // Tạo thông tin log request

      headers = response.getHeaders(); // Lấy thông tin header của response
      headersLog = hideSensitiveHeaders(headers, ignoredHeaders);
      long end = System.currentTimeMillis(); // Lấy thời gian kết thúc gọi API
      sb.append("Response : ").append(response.getStatusCode()).append(" \n")
          .append("Time     : ").append(UtilsCommon.formatDateLog(new Date())).append(" \n")
          .append("Duration : ").append(end - start).append(" ms\n")
          .append("Headers  : ").append(headersLog).append(" \n"); // Tạo thông tin log response

      StringBuilder inputSb = new StringBuilder(); // StringBuilder để lưu body của response

      String line = bufferedReader.readLine(); // Đọc dữ liệu từ input stream
      while (line != null) { // Duyệt qua dữ liệu
        inputSb.append(line); // Thêm dữ liệu vào StringBuilder
        inputSb.append("\n"); // Thêm dấu xuống dòng
        line = bufferedReader.readLine(); // Đọc dữ liệu tiếp theo
      }

      sb.append("Body     : ").append(inputSb); // Thêm body của response vào log

    } catch (Exception ex) {
      log.warn(ex.getMessage(), ex);
      sb.insert(0, "Error parsing response. Parseable data: " + request.getURI() + "\n");
    } finally {
      if (!StringUtils.isEmpty(sb.toString())) {
        log.info(sb.toString()); // Ghi log request và response
      }
    }
  }

  public void addHeader(HttpHeaders headers, String key, String value) {
    if (!headers.containsKey(key)) { // Nếu header chưa tồn tại
      headers.add(key, value); // Thêm header mới
    }
  }

  private HttpHeaders hideSensitiveHeaders(HttpHeaders headers, List<String> ignoreHeaders) {
    HttpHeaders hidedHeaders = new HttpHeaders();
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      String key = entry.getKey();
      boolean isSensitiveHeader = ignoreHeaders.stream().anyMatch(ignored -> ignored.equalsIgnoreCase(key));
      if (isSensitiveHeader) {
        hidedHeaders.put(entry.getKey(), Collections.singletonList("***"));
      } else {
        hidedHeaders.put(entry.getKey(), entry.getValue());
      }
    }
    return hidedHeaders;
  }

}
