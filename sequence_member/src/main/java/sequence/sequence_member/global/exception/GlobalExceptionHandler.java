package sequence.sequence_member.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;

@ControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 유효성 검사에서 걸리는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseData<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        // 반환할 메시지와 HTTP 상태 코드 설정
        return ResponseEntity.status(Code.VALIDATION_ERROR.getStatus()).body(ApiResponseData.of(Code.VALIDATION_ERROR.getCode(),
                ex.getBindingResult().getFieldError().getDefaultMessage(),null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseData<String>> handleCustomNotFoundException(UserNotFoundException ex){
        Code code = Code.CAN_NOT_FIND_USER;
        // 반환할 메시지와 HTTP 상태 코드 설정
        return ResponseEntity.status(code.getStatus()).body(ApiResponseData.of(code.getCode(), code.getMessage()+": "+ ex.getMessage(),null));
    }

}