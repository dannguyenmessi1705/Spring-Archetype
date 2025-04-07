package com.didan.archetype.factory.response;

import com.didan.archetype.config.MessageResponseConfig;
import com.didan.archetype.constant.ResponseStatusCode;
import com.didan.archetype.constant.ResponseStatusCodeEnum;
import com.didan.archetype.service.ErrorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

@Component
@Slf4j
public class ResponseFactory {

  @Autowired(
      required = false
  )
  private ErrorService errorService;
  @Autowired
  private MessageResponseConfig messageResponseConfig;

  private String replaceParams(String message, Map<String, String> params) { // Hàm này dùng để thay thế các tham số trong thông điệp bằng giá trị tương ứng
    if (!CollectionUtils.isEmpty(params)) { // Kiểm tra xem params có rỗng hay không
      for (Map.Entry<String, String> param : params.entrySet()) { // Duyệt qua từng tham số trong params
        message = message.replaceAll("%%" + param.getKey() + "%%", param.getValue()); // Thay thế tham số trong thông điệp bằng giá trị tương ứng
      }
    }

    if (!CollectionUtils.isEmpty(this.messageResponseConfig.getParams())) { // Kiểm tra xem messageResponseConfig có rỗng hay không
      for (Map.Entry<String, String> param : this.messageResponseConfig.getParams().entrySet()) { // Duyệt qua từng tham số trong messageResponseConfig
        message = message.replaceAll("%%" + param.getKey() + "%%", param.getValue()); // Thay thế tham số trong thông điệp bằng giá trị tương ứng
      }
    }

    return message;
  }

  private ResponseStatus parseResponseStatus(String code, Map<String, String> params) { // Hàm này dùng để phân tích mã trạng thái và tạo đối tượng ResponseStatus
    ResponseStatus responseStatus = new ResponseStatus(code, true); // Tạo một đối tượng ResponseStatus với mã trạng thái và thiết lập thông điệp mặc định
    responseStatus.setMessage(this.replaceParams(responseStatus.getMessage(), params)); // Thay thế các tham số trong thông điệp bằng giá trị tương ứng
    String errorDetail = null; // Khởi tạo biến errorDetail
    if (Objects.nonNull(this.errorService)) { // Kiểm tra xem errorService có khác null hay không
      errorDetail = this.errorService.getErrorDetail(code, LocaleContextHolder.getLocale().getLanguage()); // Lấy thông điệp lỗi từ errorService
    }

    if (Objects.nonNull(errorDetail)) { // Kiểm tra xem errorDetail có khác null hay không
      responseStatus.setDisplayMessage(this.replaceParams(errorDetail, params)); // Thay thế các tham số trong thông điệp lỗi bằng giá trị tương ứng
    } else {
      responseStatus.setDisplayMessage(responseStatus.getMessage()); // Nếu không có thông điệp lỗi từ errorService, thiết lập thông điệp hiển thị bằng thông điệp mặc định
    }

    log.debug(responseStatus.toString()); // Ghi lại thông điệp trạng thái vào log
    responseStatus.setResponseTime(new Date()); // Thiết lập thời gian phản hồi cho đối tượng ResponseStatus
    return responseStatus; // Trả về đối tượng ResponseStatus
  }

  public <T> ResponseEntity<GeneralResponse<T>> success(T data) { // Hàm này dùng để tạo phản hồi thành công với dữ liệu
    GeneralResponse<T> responseObject = new GeneralResponse<>(); // Tạo một đối tượng GeneralResponse
    responseObject.setData(data); // Thiết lập dữ liệu cho đối tượng GeneralResponse
    return this.success(responseObject); // Gọi hàm success để tạo phản hồi thành công
  }

  public <T> ResponseEntity<GeneralResponse<T>> success(GeneralResponse<T> responseObject) {
    ResponseStatus responseStatus = this.parseResponseStatus(ResponseStatusCodeEnum.SUCCESS.getCode(), null); // Tạo một đối tượng ResponseStatus với mã trạng thái thành công
    responseObject.setStatus(responseStatus); // Thiết lập trạng thái cho đối tượng GeneralResponse
    return ResponseEntity.ok().body(responseObject); // Trả về phản hồi thành công với đối tượng GeneralResponse
  }

  public <T> ResponseEntity<GeneralResponse<T>> successWithHeader(MultiValueMap<String, String> header, T data) { // Hàm này dùng để tạo phản hồi thành công với headers
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    responseObject.setData(data);
    ResponseStatus responseStatus = this.parseResponseStatus(ResponseStatusCodeEnum.SUCCESS.getCode(), null);
    responseObject.setStatus(responseStatus);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.addAll(header);
    return ResponseEntity.ok().headers(responseHeaders).body(responseObject);
  }

  public <T> ResponseEntity<GeneralResponse<T>> fail(T data, ResponseStatusCode code) { // Hàm này dùng để tạo phản hồi thất bại với dữ liệu
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    responseObject.setData(data);
    return fail(responseObject, code, null); // Gọi hàm fail để tạo phản hồi thất bại với dữ liệu
  }

  public <T> ResponseEntity<GeneralResponse<T>> fail(ResponseStatusCode code) { // Hàm này dùng để tạo phản hồi thất bại với mã trạng thái
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    return fail(responseObject, code, null);
  }

  public <T> ResponseEntity<GeneralResponse<T>> fail(GeneralResponse<T> responseObject, ResponseStatusCode code) { // Hàm này dùng để tạo phản hồi thất bại với đối tượng GeneralResponse
    if (Objects.isNull(responseObject)) {
      responseObject = new GeneralResponse<>();
    }

    return fail(responseObject, code, null);
  }

  public <T> ResponseEntity<GeneralResponse<T>> fail(GeneralResponse<T> responseObject, ResponseStatusCode code, Map<String, String> params) { // Hàm này dùng để tạo phản hồi thất bại với đối tượng
    // GeneralResponse và mã trạng thái
    ResponseStatus responseStatus = parseResponseStatus(code.getCode(), params);
    if (Objects.isNull(responseObject)) {
      responseObject = new GeneralResponse<>();
    }

    responseObject.setStatus(responseStatus);
    return ResponseEntity.status(code.getHttpCode()).body(responseObject);
  }

  public <T> void httpServletResponseToClient(HttpServletResponse httpServletResponse, T data, ResponseStatusCode statusCode) throws IOException { // Hàm này dùng để gửi phản hồi về phía client
    this.httpServletResponseToClient(httpServletResponse, data, statusCode, null);
  }

  public <T> void httpServletResponseToClient(HttpServletResponse httpServletResponse, T data, ResponseStatusCode statusCode, Map<String, String> params) throws IOException { // Hàm này dùng để gửi phản hồi về phía client với dữ liệu và mã trạng thái
    GeneralResponse<T> response = new GeneralResponse<>();
    response.setData(data);
    ResponseStatus responseStatus = this.parseResponseStatus(statusCode.getCode(), params);
    response.setStatus(responseStatus);
    this.writeToHttpServletResponse(httpServletResponse, response, statusCode);
  }

  public void writeToHttpServletResponse(HttpServletResponse httpServletResponse, Object response, ResponseStatusCode statusCode) throws IOException { // Hàm này dùng để ghi phản hồi vào HttpServletResponse
    ObjectMapper mapper = new ObjectMapper();
    String responseString = mapper.writeValueAsString(response);
    httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
    httpServletResponse.setStatus(statusCode.getHttpCode());
    httpServletResponse.setHeader("Content-Type", "application/json");
    httpServletResponse.getWriter().write(responseString);
    httpServletResponse.getWriter().flush();
    httpServletResponse.getWriter().close();
  }
}
