package com.didan.archetype.filter;

import com.didan.archetype.constant.TrackingContextEnum;
import com.didan.archetype.utils.UtilsCommon;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@Slf4j
@Order(0)
public class ClientDataFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    String appVersion = httpServletRequest.getHeader(TrackingContextEnum.APP_VERSION.getHeaderKey());
    String osVersion = httpServletRequest.getHeader(TrackingContextEnum.OS_VERSION.getHeaderKey());
    String typeOS = httpServletRequest.getHeader(TrackingContextEnum.TYPE_OS.getHeaderKey());
    String userAgent = httpServletRequest.getHeader(TrackingContextEnum.USER_AGENT.getHeaderKey());

    ThreadContext.put(TrackingContextEnum.APP_VERSION.getThreadKey(), appVersion);
    ThreadContext.put(TrackingContextEnum.TYPE_OS.getThreadKey(), typeOS);
    ThreadContext.put(TrackingContextEnum.OS_VERSION.getThreadKey(), osVersion);
    ThreadContext.put(TrackingContextEnum.USER_AGENT.getThreadKey(), userAgent);
    ThreadContext.put(TrackingContextEnum.IMEI_APP.getThreadKey(),
        httpServletRequest.getHeader(TrackingContextEnum.IMEI_APP.getHeaderKey()));
    String xOriginalForwardFor = httpServletRequest.getHeader(TrackingContextEnum.X_ORIGINAL_FORWARD_FOR.getHeaderKey());
    String ip = httpServletRequest.getHeader(TrackingContextEnum.X_FORWARD_FOR.getHeaderKey());
    if (StringUtils.isNotBlank(xOriginalForwardFor)) {
      try {
        ip = xOriginalForwardFor.split(",")[0];
      } catch (Exception e) {
        log.error("Exception when parse IP", e);
      }
    } else {
      String xRealIp = httpServletRequest.getHeader(TrackingContextEnum.X_REAL_IP.getHeaderKey());
      ip = StringUtils.isBlank(xRealIp) ? httpServletRequest.getRemoteAddr() : xRealIp;
    }
    ThreadContext.put(TrackingContextEnum.X_REAL_IP.getThreadKey(), ip);
    ThreadContext.put(TrackingContextEnum.PROCESS.getThreadKey(), httpServletRequest.getRequestURI());
    if (StringUtils.isEmpty(httpServletRequest.getHeader(HttpHeaders.ACCEPT_LANGUAGE))) {
      LocaleContextHolder.setLocale(new Locale.Builder().setLanguage("vi").setRegion("VN").build());
    }
    generateCorrelationIdIfNotExists(httpServletRequest.getHeader(TrackingContextEnum.X_REQUEST_ID.getHeaderKey()));
    httpServletResponse.setHeader(TrackingContextEnum.X_REQUEST_ID.getHeaderKey(),
        ThreadContext.get(TrackingContextEnum.X_REQUEST_ID.getThreadKey()));

    filterChain.doFilter(httpServletRequest, httpServletResponse);
    ThreadContext.clearAll();
  }

  private void generateCorrelationIdIfNotExists(String xRequestId) {
    String requestId =
        StringUtils.isBlank(xRequestId) ? UtilsCommon.createNewUUID() : xRequestId;
    ThreadContext.put(TrackingContextEnum.X_REQUEST_ID.getThreadKey(), requestId);
  }
}
