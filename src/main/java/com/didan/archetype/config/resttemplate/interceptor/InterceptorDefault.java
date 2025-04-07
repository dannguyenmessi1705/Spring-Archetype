package com.didan.archetype.config.resttemplate.interceptor;

import com.didan.archetype.config.properties.AppConfigProperties;
import com.didan.archetype.config.resttemplate.RestTemplateTracer;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InterceptorDefault implements ClientHttpRequestInterceptor {

  private final AppConfigProperties appConfigProperties;
  private final RestTemplateTracer restTemplateTracer;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    try {
      HttpHeaders headers = request.getHeaders();
      restTemplateTracer.addHeader(headers, "Accept-Language", appConfigProperties.getDefaultLanguage());
    } catch (Exception ex) {
      log.error("Interceptor Rest error: {}", ex.getMessage(), ex);
    }

    ClientHttpResponse httpResponse = execution.execute(request, body);
    if (appConfigProperties.isDefaultServiceEnableLogRequest()) {
      restTemplateTracer.trace(request, body, httpResponse, new Date());
    }
    return httpResponse;
  }
}
