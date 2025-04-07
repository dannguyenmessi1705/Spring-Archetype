package com.didan.archetype.filter;

import com.didan.archetype.config.properties.AppConfigProperties;
import com.didan.archetype.constant.ResponseStatusCodeEnum;
import com.didan.archetype.constant.TrackingContextEnum;
import com.didan.archetype.factory.LogSystemFactory;
import com.didan.archetype.factory.response.GeneralResponse;
import com.didan.archetype.factory.response.ResponseStatus;
import com.didan.archetype.utils.UtilsCommon;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Configuration
@Slf4j
@Order(2)
public class LogCorrelationFilter extends OncePerRequestFilter {

  private final AppConfigProperties appConfigProperties;

  @Value("${app.server.errors:#{null}}")
  private String serverErrorsCodes;

  private List<String> serverErrors;

  public LogCorrelationFilter(AppConfigProperties appConfigProperties) {
    this.appConfigProperties = appConfigProperties;
    serverErrors = new ArrayList<>();
    if (StringUtils.hasText(serverErrorsCodes)) {
      serverErrorsCodes = serverErrorsCodes.replaceAll("\\s+", "");
      serverErrors = Arrays.asList(serverErrorsCodes.split(","));
    }
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

    String uri = request.getRequestURI();
    if (UtilsCommon.isIgnoredUri(uri)) {
      chain.doFilter(request, response);
      return;
    }

    long time = System.currentTimeMillis();
    String correlationId = generateCorrelationIdIfNotExists(
        request.getHeader(TrackingContextEnum.X_CORRELATION_ID.getHeaderKey()));
    response.setHeader(TrackingContextEnum.X_CORRELATION_ID.getHeaderKey(),
        ThreadContext.get(TrackingContextEnum.X_CORRELATION_ID.getThreadKey()));

    request = new RequestWrapper(request);

    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
    String method = request.getMethod();

    chain.doFilter(request, responseWrapper);

    if (!uri.startsWith("/actuator") && !uri.contains("/websocket")) {
      String responseBody = new String(responseWrapper.getContentAsByteArray());

      ResponseStatus status = getApplicationErrorCode(responseBody, response);

      if (status != null) {
        String errorType = getErrorType(status);
        long now = System.currentTimeMillis();
        long duration;
        String durationString = ThreadContext.get(TrackingContextEnum.DURATION.getThreadKey());
        duration = now - time;
        if (StringUtils.hasText(durationString)) {
          try {
            duration = Long.parseLong(durationString);
          } catch (NumberFormatException ex) {
            log.error("Error when parse duration", ex);
          }
        }
        LogSystemFactory logSystemFactory = LogSystemFactory.builder()
            .applicationCode(appConfigProperties.getApplicationContextName())
            .requestID(ThreadContext.get(TrackingContextEnum.X_REQUEST_ID.getThreadKey()))
            .sessionID(ThreadContext.get(TrackingContextEnum.TRACE_ID.getThreadKey()))
            .requestContent(UtilsCommon.hideSensitiveData(((RequestWrapper) request).getBody()))
            .responseContent(getBodyResponse(responseBody, response))
            .startTime(time)
            .endTime(now)
            .duration(duration)
            .errorCode(status.getCode())
            .errorDescription(status.getMessage())
            .transactionType(errorType)
            .actionName(method)
            .username(ThreadContext.get(TrackingContextEnum.USER.getThreadKey()))
            .account(ThreadContext.get(TrackingContextEnum.MSISDN.getThreadKey()))
            .transactionID(uri)
            .client(request.getHeader(TrackingContextEnum.CLIENT.getHeaderKey()))
            .spanId(ThreadContext.get(TrackingContextEnum.SPAN_ID.getThreadKey()))
            .clientIP(ThreadContext.get(TrackingContextEnum.X_REAL_IP.getThreadKey()))
            .osVersion(ThreadContext.get(TrackingContextEnum.OS_VERSION.getThreadKey()))
            .userAgent(ThreadContext.get(TrackingContextEnum.USER_AGENT.getThreadKey()))
            .imei(ThreadContext.get(TrackingContextEnum.IMEI_APP.getThreadKey()))
            .imeiTel(ThreadContext.get(TrackingContextEnum.IMEI_TELE.getThreadKey()))
            .typeOS(ThreadContext.get(TrackingContextEnum.TYPE_OS.getThreadKey()))
            .appVersion(ThreadContext.get(TrackingContextEnum.APP_VERSION.getThreadKey()))
            .correlationID(ThreadContext.get(TrackingContextEnum.X_CORRELATION_ID.getThreadKey()))
            .build();
        log.info("STANDARD_LOG: {}", UtilsCommon.GSON.toJson(logSystemFactory));
        log.info(
            "REPORT|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",
            appConfigProperties.getApplicationShortName(),
            method,
            uri,
            response.getStatus(),
            errorType,
            status.getCode(),
            correlationId,
            ThreadContext.get(TrackingContextEnum.MSISDN.getThreadKey()),
            System.currentTimeMillis() - time,
            status.getMessage()
        );
      }
    }
    responseWrapper.copyBodyToResponse();

    log.info("{}: {} ms ", request.getRequestURI(), System.currentTimeMillis() - time);
  }

  private String getBodyResponse(String responseBody, HttpServletResponse response) {
    if (response.containsHeader("Content-Disposition")) {
      return "FILE";
    }
    return UtilsCommon.hideSensitiveData(responseBody);
  }

  private String getErrorType(ResponseStatus status) {
    if (ResponseStatusCodeEnum.SUCCESS.getCode().equals(status.getCode())) {
      return "Success";
    } else if (serverErrors.contains(status.getCode())) {
      return "ErrorServer";
    } else {
      return "ErrorClient";
    }
  }

  private ResponseStatus getApplicationErrorCode(String responseBody, HttpServletResponse response) {
    try {
      if (!StringUtils.hasText(responseBody)) {
        return null;
      } else {
        return Objects.requireNonNull(UtilsCommon.GSON.fromJson(responseBody, GeneralResponse.class)).getStatus();
      }
    } catch (Exception ex) {
      if (response.containsHeader("Content-Disposition") && response.getStatus() == HttpServletResponse.SC_OK) {
        return new ResponseStatus(ResponseStatusCodeEnum.SUCCESS.getCode(), true);
      }
      return null;
    }
  }

  private String generateCorrelationIdIfNotExists(String xCorrelationId) {
    String correlationId =
        !StringUtils.hasText(xCorrelationId) ? String.format("%s-%s", appConfigProperties.getApplicationShortName(), UUID.randomUUID().toString().replace("-", "").toLowerCase()).trim() :
            xCorrelationId;
    ThreadContext.put(TrackingContextEnum.X_CORRELATION_ID.getThreadKey(), correlationId);
    return correlationId;
  }
}