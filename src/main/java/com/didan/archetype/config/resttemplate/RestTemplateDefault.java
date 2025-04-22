package com.didan.archetype.config.resttemplate;

import com.didan.archetype.config.resttemplate.interceptor.InterceptorDefault;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

  @Value("${rest.connection.connection-request-timeout}")
  private long connectionRequestTimeout;

  @Value("${rest.connection.connection-response-timeout}")
  private long connectionResponseTimeout;

  @Value("${rest.connection-pool.connection-timeout}")
  private long connectionTimeout;

  @Primary
  @Bean({"restDefault"})
  public RestTemplate restTemplateDefault(
      InterceptorDefault interceptorDefault,
      @Qualifier("defaultHttpRequestFactory") BufferingClientHttpRequestFactory requestFactory) {
    return RestTemplateUtils.createRestTemplate(interceptorDefault, requestFactory);
  }

  @Bean({"restProxyDefault"})
  public RestTemplate restProxyDefault(
      InterceptorDefault interceptorDefault,
      @Qualifier("proxyHttpRequestFactory") BufferingClientHttpRequestFactory requestFactory) {
    return RestTemplateUtils.createRestTemplate(interceptorDefault, requestFactory);
  }


  @Bean
  @ConfigurationProperties(prefix = "rest.default.connection-pool")
  public PoolingHttpClientConnectionManager commonHttpPoolingConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = RestTemplateUtils.poolingHttpClientConnectionManager();
    poolingHttpClientConnectionManager.setDefaultConnectionConfig(ConnectionConfig.custom().setConnectTimeout(connectionTimeout, TimeUnit.SECONDS).build());
    return poolingHttpClientConnectionManager;
  }

  @Bean
  public BufferingClientHttpRequestFactory proxyHttpRequestFactory(PoolingHttpClientConnectionManager commonHttpPoolingConnectionManager) {
    HttpClientBuilder builder = clientBuilder(commonHttpPoolingConnectionManager);
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
    CloseableHttpClient closeableHttpClient = builder.build();
    return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(closeableHttpClient));
  }

  @Bean
  public BufferingClientHttpRequestFactory defaultHttpRequestFactory(PoolingHttpClientConnectionManager commonHttpPoolingConnectionManager) {
    CloseableHttpClient closeableHttpClient = clientBuilder(commonHttpPoolingConnectionManager).build();
    return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(closeableHttpClient));
  }

  private HttpClientBuilder clientBuilder(PoolingHttpClientConnectionManager commonHttpPoolingConnectionManager) {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.SECONDS)
        .setResponseTimeout(connectionResponseTimeout, TimeUnit.SECONDS)
        .build();
    return HttpClients.custom()
        .setConnectionManager(commonHttpPoolingConnectionManager)
        .setConnectionManagerShared(true)
        .evictIdleConnections(TimeValue.ofSeconds(30L))
        .evictExpiredConnections()
        .setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ofSeconds(0L)))
        .setDefaultRequestConfig(requestConfig)
        .disableRedirectHandling();
  }
}
