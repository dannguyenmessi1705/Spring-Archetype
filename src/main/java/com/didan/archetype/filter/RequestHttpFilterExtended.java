package com.didan.archetype.filter;

import com.didan.archetype.filter.cachehttp.CachedBodyHttpServletRequest;
import com.didan.archetype.utils.UtilsCommon;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Configuration
@Slf4j
@Order(1)
@ConditionalOnProperty(value = "app.log-request-http", havingValue = "true") // Kiểm tra xem thuộc tính này có được cấu hình là true không, nếu có thì mới áp dụng filter này
public class RequestHttpFilterExtended extends OncePerRequestFilter {

  Map<String, String> replaceCharsError = new HashMap<>();
  @Value("${app.log-request-http.ignored-headers:#{null}")
  private String ignoredHeadersConfig; // Các header cần ẩn trong log
  private List<String> ignoredHeaders;

  public RequestHttpFilterExtended() {
    replaceCharsError.put("\u0000", "");
    ignoredHeaders = new ArrayList<>();
    if (!StringUtils.isEmpty(ignoredHeadersConfig)) { // Nếu có cấu hình các header cần ẩn
      ignoredHeadersConfig = ignoredHeadersConfig.replaceAll("\\s+", ""); // Xóa khoảng trắng
      ignoredHeaders = Arrays.asList(ignoredHeadersConfig.split(",")); // Tách các header thành danh sách
    }
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
    StringBuilder str = new StringBuilder();
    try {
      String uri = request.getRequestURI();
      if (UtilsCommon.isIgnoredUri(uri)) {
        chain.doFilter(request, response);
        return;
      }

      ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
      CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(new ContentCachingRequestWrapper(request));
      // request
      Map<String, String> headers = cachedBodyHttpServletRequest.getHeaders();
      Map<String, String> logHeaders = new HashMap<>();
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        if (ignoredHeaders.contains(entry.getKey())) {
          logHeaders.put(entry.getKey(), "***");
        } else {
          logHeaders.put(entry.getKey(), entry.getValue());
        }
      }

      str.append("Request = ").append(" \n")
          .append("Request to: ").append(getFullURL(cachedBodyHttpServletRequest)).append(" \n")
          .append("Method    : ").append(cachedBodyHttpServletRequest.getMethod()).append(" \n")
          .append("Header    : ").append(logHeaders).append(" \n")
          .append("Body      : ").append(replaceChars(getBodyRequest(cachedBodyHttpServletRequest))).append(" \n")
          .append(" \n");

      if (UtilsCommon.isMultipart(request)) {
        chain.doFilter(request, responseWrapper);
      } else {
        chain.doFilter(cachedBodyHttpServletRequest, responseWrapper);
      }

      // response
      str.append("Response = ").append(" \n")
          .append("Status code: ").append(responseWrapper.getStatus()).append(" \n")
          .append("Headers    : ").append(getHeaders(responseWrapper)).append(" \n")
          .append("Body       : ").append(getBodyResponse(responseWrapper)).append(" \n");
      responseWrapper.copyBodyToResponse();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      str.insert(0, "Error parsing response. Parseable data: \n");
    } finally {
      if (!StringUtils.isEmpty(str.toString())) {
        log.info(str.toString());
      }
    }

  }

  public String getBodyResponse(HttpServletResponse responseWrapper) {
    if (responseWrapper.containsHeader("Content-Disposition")) {
      return "FILE";
    }
    String body = new String(((ContentCachingResponseWrapper) responseWrapper).getContentAsByteArray());
    return StringUtils.isEmpty(body) ? "" : UtilsCommon.hideSensitiveData(body);
  }

  public Map<String, String> getHeaders(HttpServletResponse response) {
    Map<String, String> map = new HashMap<>();
    Collection<String> headersName = response.getHeaderNames();
    for (String s : headersName) {
      if (ignoredHeaders.contains(s)) {
        map.put(s, "***");
      } else {
        map.put(s, response.getHeader(s));
      }
    }

    return map;
  }

  public String getFullURL(HttpServletRequest request) {
    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURL.toString();
    } else {
      return requestURL.append('?').append(queryString).toString();
    }
  }

  public String replaceChars(String str) {
    for (Map.Entry<String, String> entry : replaceCharsError.entrySet()) {
      str = str.replace(entry.getKey(), entry.getValue());
    }
    return str;
  }

  public String getBodyRequest(CachedBodyHttpServletRequest request) {
    if (UtilsCommon.isMultipart(request)) {
      return "FILE";
    }
    boolean checkBodyRequest = request.getCachedBody() != null;
    return checkBodyRequest ? UtilsCommon.hideSensitiveData(new String(request.getCachedBody(), StandardCharsets.UTF_8)) : "";
  }

}
