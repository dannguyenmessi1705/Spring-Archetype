package com.didan.archetype.exception;

import com.didan.archetype.constant.ResponseStatusCode;
import com.didan.archetype.constant.ResponseStatusCodeEnum;
import com.didan.archetype.factory.response.GeneralResponse;
import com.didan.archetype.factory.response.ResponseFactory;
import com.didan.archetype.factory.response.ResponseStatus;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice // Cung cấp xử lý ngoại lệ toàn cục cho các controller
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final Map<String, ResponseStatusCode> handleHttpMessageNotReadableListError = new HashMap<>();

  @Autowired
  ResponseFactory responseFactory; // Sử dụng ResponseFactory để tạo ra các phản hồi cho các lỗi khác nhau

  public GlobalExceptionHandler() {
    this.handleHttpMessageNotReadableListError.put("JSON parse error", ResponseStatusCodeEnum.ERROR_BODY_CLIENT); // Thêm mã lỗi JSON parse error vào danh sách xử lý
    this.handleHttpMessageNotReadableListError.put("Required request body is missing", ResponseStatusCodeEnum.ERROR_BODY_REQUIRED); // Thêm mã lỗi Required request body is missing vào danh sách xử lý
  }

  @ExceptionHandler({BaseResponseException.class}) // Xử lý ngoại lệ BaseResponseException
  public ResponseEntity<?> handleValidationExceptions(BaseResponseException ex) { // Phương thức xử lý ngoại lệ
    try {
      if (Objects.isNull(ex.getParams())) { // Nếu không có tham số nào được truyền vào
        return responseFactory.fail(ex.getGeneralResponse(), ex.getResponseStatusCode()); // Trả về phản hồi thất bại với mã trạng thái và thông điệp lỗi
      } else { // Nếu có tham số được truyền vào
        return Objects.isNull(ex.getGeneralResponse()) ? // Nếu không có phản hồi nào được truyền vào
            responseFactory.fail(new GeneralResponse<>(), ex.getResponseStatusCode(), ex.getParams()) // Trả về phản hồi thất bại với mã trạng thái và tham số
            : responseFactory.fail(ex.getGeneralResponse(), ex.getResponseStatusCode(), ex.getParams()); // Trả về phản hồi thất bại với mã trạng thái và thông điệp lỗi
      }
    } catch (Exception e) { // Nếu có lỗi xảy ra trong quá trình xử lý
      return responseFactory.fail(ResponseStatusCodeEnum.INTERNAL_GENERAL_SERVER_ERROR); // Trả về phản hồi thất bại với mã trạng thái INTERNAL_GENERAL_SERVER_ERROR
    }
  }

  @ExceptionHandler({Exception.class, RuntimeException.class}) // Xử lý ngoại lệ Exception và RuntimeException
  public final ResponseEntity<Object> handleAllExceptions(Exception ex) { // Phương thức xử lý ngoại lệ
    log.error("Exception: ", ex); // Ghi lại thông tin lỗi vào log
    return createResponse(ResponseStatusCodeEnum.INTERNAL_GENERAL_SERVER_ERROR); // Trả về phản hồi thất bại với mã trạng thái INTERNAL_GENERAL_SERVER_ERROR
  }

  @ExceptionHandler({BusinessException.class})
  public final ResponseEntity<Object> handleValidationExceptions(RuntimeException ex) {
    return this.createResponse(ResponseStatusCodeEnum.BUSINESS_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage())); // Lặp qua tất cả các lỗi trong BindingResult và thêm chúng vào Map errors
    return createResponse(ResponseStatusCodeEnum.VALIDATION_ERROR, errors); // Trả về phản hồi thất bại với mã trạng thái VALIDATION_ERROR và các lỗi đã thu thập
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) { // Xử lý ngoại lệ
    // HttpMessageNotReadableException xảy ra khi không thể đọc được thông điệp HTTP
    if (Objects.nonNull(ex.getMessage())) { // Nếu thông điệp không null
      Optional<ResponseStatusCode> responseStatusCode = this.handleHttpMessageNotReadableListError.entrySet().stream() // Lặp qua danh sách các mã lỗi
          .filter(stringResponseStatusCodeEntry ->
              ex.getMessage().contains(stringResponseStatusCodeEntry.getKey()))
          .map(Map.Entry::getValue).findFirst(); // Tìm mã lỗi đầu tiên trong danh sách mà thông điệp chứa nó
      if (responseStatusCode.isPresent()) { // Nếu tìm thấy mã lỗi
        return this.createResponse(responseStatusCode.get()); // Trả về phản hồi thất bại với mã trạng thái tìm thấy
      }
    }

    return handleExceptionInternal(ex, null, headers, status, request); // Nếu không tìm thấy mã lỗi, gọi phương thức handleExceptionInternal để xử lý ngoại lệ
  }

  private ResponseEntity<Object> createResponse(ResponseStatusCode response) {
    ResponseStatus responseStatus = new ResponseStatus(response.getCode(), true);
    responseStatus.setResponseTime(new Date());
    GeneralResponse<Object> responseObject = new GeneralResponse<>();
    responseObject.setStatus(responseStatus);
    return new ResponseEntity<>(responseObject, HttpStatus.valueOf(response.getHttpCode()));
  }

  private ResponseEntity<Object> createResponse(ResponseStatusCode response, Object errors) {
    ResponseStatus responseStatus = new ResponseStatus(response.getCode(), true);
    responseStatus.setResponseTime(new Date());
    GeneralResponse<Object> responseObject = new GeneralResponse<>();
    responseObject.setStatus(responseStatus);
    responseObject.setData(errors);
    return new ResponseEntity<>(responseObject, HttpStatus.valueOf(response.getHttpCode()));
  }
}
