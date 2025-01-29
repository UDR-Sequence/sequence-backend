package sequence.sequence_member.global.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    // 메서드 인자 타입 불일치 예외 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseData<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        // 반환할 메시지와 HTTP 상태 코드 설정
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseData.of(Code.VALIDATION_ERROR.getCode(),
                ex.getName()+" 은 반드시 해당타입이여야합니다. : "+ex.getRequiredType().getName(),null));
    }

    //HTTP 요청의 메시지 바디를 읽을 수 없을 때 발생하는 예외. 예를 들어, JSON 파싱 에러나 요청 바디가 비어 있는 경우 발생.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseData<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Code code = Code.INVALID_INPUT;
        // todo : 배포시에는 기본 메시지만 보내도록 수정
        String baseMessage = code.getDetailMessage(e.getMessage());
        String resultMessage = baseMessage;
        // JSON 파싱 오류에 대한 세부 메시지 처리
        /**
         * enum타입에 맞지 않는 정보가 있을경우. 응답 예시
         * {
         *     "code": 40005,
         *     "message": "읽을 수 없는 데이터입니다. : JSON parse error: Cannot deserialize value of type `com.example.danjamserver.roomMate.enums.Level` from String \"많이타요\": not one of the values accepted for Enum class: [HIGH, LOW, NO_MATTER]: hotLevel "
         * }
         */
        if (e.getCause() instanceof JsonMappingException) {
            JsonMappingException jsonMappingException = (JsonMappingException) e.getCause();
            StringBuilder detailedMessage = new StringBuilder(baseMessage);
            detailedMessage.append(": ");

            jsonMappingException.getPath().forEach(reference -> {
                detailedMessage.append(reference.getFieldName()).append(" ");
            });

            resultMessage = detailedMessage.toString();
        }

        ApiResponseData<String> response = ApiResponseData.of(code.getCode(), resultMessage, null);
        return ResponseEntity.status(code.getStatus()).body(response);
    }

/**
 * 커스텀 오류 처리
* ---------------------------------------------------------------------------------------------------
*
*/
    // UserNotFoundException 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseData<String>> handleCustomNotFoundException(UserNotFoundException ex){
        Code code = Code.CAN_NOT_FIND_USER;
        // 반환할 메시지와 HTTP 상태 코드 설정
        return ResponseEntity.status(code.getStatus()).body(ApiResponseData.of(code.getCode(), code.getMessage()+": "+ ex.getMessage(),null));
    }

}