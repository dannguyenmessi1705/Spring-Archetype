package com.didan.archetype.controller.restful;

import com.didan.archetype.config.properties.AppConfigProperties;
import com.didan.archetype.constant.ResponseStatusCodeEnum;
import com.didan.archetype.factory.response.GeneralResponse;
import com.didan.archetype.locale.Translator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

  @ResponseStatus(HttpStatus.BAD_REQUEST) // Trả về mã lỗi 400
  @ExceptionHandler({MethodArgumentNotValidException.class}) // Xử lý ngoại lệ MethodArgumentNotValidException
  public <T> GeneralResponse<T> handleValidationExceptions(MethodArgumentNotValidException ex) { // Phương thức xử lý ngoại lệ là một ngoại lệ được ném ra khi có lỗi trong việc ràng buộc tham số phương thức
    GeneralResponse<T> errors = new GeneralResponse<>(); // Tạo một đối tượng GeneralResponse
    com.didan.archetype.factory.response.ResponseStatus status = new com.didan.archetype.factory.response.ResponseStatus(); // Tạo một đối tượng ResponseStatus
    status.setCode(ResponseStatusCodeEnum.VALIDATION_ERROR.getCode()); // Thiết lập mã lỗi
    List<ObjectError> objectError = ex.getBindingResult().getAllErrors(); // Lấy danh sách các lỗi từ BindingResult
    if (!objectError.isEmpty()) { // Nếu danh sách lỗi không rỗng
      status.setMessage((objectError.get(0)).getDefaultMessage()); // Lấy thông điệp lỗi đầu tiên
    }

    status.setResponseTime(new Date()); // Thiết lập thời gian phản hồi
    errors.setStatus(status); // Thiết lập trạng thái cho đối tượng GeneralResponse
    return errors; // Trả về đối tượng GeneralResponse
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST) // Trả về mã lỗi 400
  @ExceptionHandler({ServletRequestBindingException.class}) // Xử lý ngoại lệ ServletRequestBindingException là một ngoại lệ được ném ra khi có lỗi trong việc ràng buộc yêu cầu HTTP
  public <T> GeneralResponse<T> handleValidationExceptions(ServletRequestBindingException ex) {
    GeneralResponse<T> errors = new GeneralResponse<>();
    com.didan.archetype.factory.response.ResponseStatus status = new com.didan.archetype.factory.response.ResponseStatus(); // Tạo một đối tượng ResponseStatus
    status.setCode(ResponseStatusCodeEnum.VALIDATION_ERROR.getCode()); // Thiết lập mã lỗi
    status.setMessage(ex.getMessage()); // Lấy thông điệp lỗi từ ngoại lệ
    errors.setStatus(status); // Thiết lập trạng thái cho đối tượng GeneralResponse
    status.setResponseTime(new Date()); // Thiết lập thời gian phản hồi
    return errors;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST) // Trả về mã lỗi 400
  @ExceptionHandler({ConstraintViolationException.class}) // Xử lý ngoại lệ ConstraintViolationException là một ngoại lệ được ném ra khi có lỗi ràng buộc trong quá trình xác thực dữ liệu
  public <T> GeneralResponse<T> handleValidationException1s(ConstraintViolationException ex) { // Phương thức xử lý ngoại lệ
    GeneralResponse<T> errors = new GeneralResponse<>(); // Tạo một đối tượng GeneralResponse
    com.didan.archetype.factory.response.ResponseStatus status = new com.didan.archetype.factory.response.ResponseStatus(); // Tạo một đối tượng ResponseStatus
    status.setResponseTime(new Date()); // Thiết lập thời gian phản hồi
    status.setCode(ResponseStatusCodeEnum.VALIDATION_ERROR.getCode()); // Thiết lập mã lỗi
    ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().stream().findFirst().orElse((ConstraintViolation<?>) null); // Lấy lỗi ràng buộc đầu tiên từ danh sách các lỗi ràng buộc
    if (constraintViolation == null) { // Nếu không có lỗi ràng buộc nào
      status.setMessage(Translator.toLocale(ResponseStatusCodeEnum.VALIDATION_ERROR.getCode())); // Lấy thông điệp lỗi từ Translator
    } else {
      status.setMessage(constraintViolation.getMessage()); // Lấy thông điệp lỗi từ lỗi ràng buộc
    }

    errors.setStatus(status); // Thiết lập trạng thái cho đối tượng GeneralResponse
    return errors;
  }
}
