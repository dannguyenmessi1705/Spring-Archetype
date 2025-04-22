package com.didan.archetype.config.resttemplate;

import com.didan.archetype.config.resttemplate.interceptor.InterceptorDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@UtilityClass
@Slf4j
public class RestTemplateUtils {

  /**
   * Tạo một RestTemplate với các interceptor và bộ chuyển đổi tin nhắn đã được cấu hình
   * @param interceptorDefault
   * @param requestFactory
   * @return
   */
  public static RestTemplate createRestTemplate(InterceptorDefault interceptorDefault, BufferingClientHttpRequestFactory requestFactory) {
    RestTemplate restTemplate = new RestTemplate(); // Tạo một RestTemplate mới
    restTemplate.setInterceptors(Collections.singletonList(interceptorDefault)); // Thiết lập interceptor cho RestTemplate
    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // Thêm StringHttpMessageConverter với mã hóa UTF-8 vào danh sách các bộ chuyển đổi tin nhắn
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError(); // Kiểm tra xem phản hồi có lỗi hay không nếu trạng thái là 4xx hoặc 5xx
      }
    }); // Thiết lập một DefaultResponseErrorHandler để xử lý lỗi trong phản hồi
    restTemplate.setRequestFactory(requestFactory);
    return restTemplate;
  } // Tạo một RestTemplate với các interceptor và bộ chuyển đổi tin nhắn đã được cấu hình

  /**
   * Tạo một PoolingHttpClientConnectionManager với cấu hình SSL để tin cậy tất cả các chứng chỉ
   * @return
   * @throws NoSuchAlgorithmException
   * @throws KeyStoreException
   * @throws KeyManagementException
   */
  public static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    return PoolingHttpClientConnectionManagerBuilder.create()
        .setTlsSocketStrategy(
            new DefaultClientTlsStrategy(
                SSLContextBuilder.create()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build(),
                HostnameVerificationPolicy.BOTH,
                NoopHostnameVerifier.INSTANCE
            )).build();
  }
}
