package com.didan.archetype.config.resttemplate;

import com.didan.archetype.config.properties.AppConfigProperties;
import com.didan.archetype.config.resttemplate.interceptor.InterceptorDefault;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
@Data
public class RestTemplateDefault {

  @Value("${app.proxy.host:#{null}}") // Đọc giá trị từ file cấu hình với key là "app.proxy.host")
  private String proxyHost; // Địa chỉ host của proxy

  @Value("${app.proxy.port:#{null}}") // Đọc giá trị từ file cấu hình với key là "app.proxy.port")
  private Integer proxyPort; // Cổng của proxy

  @Value("${app.proxy.username:#{null}}") // Đọc giá trị từ file cấu hình với key là "app.proxy.username")
  private String proxyUsername; // Tên người dùng của proxy

  @Value("${app.proxy.password:#{null}}") // Đọc giá trị từ file cấu hình với key là "app.proxy.password")
  private String proxyPassword; // Mật khẩu của proxy

  @Primary
  @Bean({"restDefault"})
  public RestTemplate restTemplateDefault(InterceptorDefault interceptorDefault) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    HttpClientBuilder builder = createHttpClientBuilder();
    CloseableHttpClient httpClient = builder.build();
    BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    return RestTemplateUtils.createRestTemplate(interceptorDefault, requestFactory);
  }

  @Bean({"restProxyDefault"})
  public RestTemplate restProxyDefault(InterceptorDefault interceptorDefault) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    HttpClientBuilder builder = createHttpClientBuilder();
    CloseableHttpClient httpClient = builder.build();
    BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    return RestTemplateUtils.createRestTemplate(interceptorDefault, requestFactory);
  }

  private HttpClientBuilder createHttpClientBuilder() {
    HttpClientBuilder builder = HttpClientBuilder.create();
    setAuthProxy(builder);
    return builder;
  } // Create a default HttpClientBuilder with connection pooling

  private void setAuthProxy(HttpClientBuilder builder) {
    if (!StringUtils.isBlank(proxyHost) && !StringUtils.isBlank(String.valueOf(proxyPort))) {
      HttpHost httpHost = new HttpHost(proxyHost, proxyPort);
      builder.setRoutePlanner(new DefaultProxyRoutePlanner(httpHost));

      if (!StringUtils.isBlank(proxyUsername) && !StringUtils.isBlank(proxyPassword)) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(proxyHost, proxyPort),
            new UsernamePasswordCredentials(proxyUsername, proxyPassword.toCharArray())
        );
        builder.setDefaultCredentialsProvider(credentialsProvider).disableCookieManagement();
      }
    }
  } // Set up proxy authentication if needed
}
