package sequence.sequence_member.global.exception;

import lombok.extern.slf4j.Slf4j;
import sequence.sequence_member.global.response.Code;

@Slf4j
public class AuthException extends BaseException {
    public AuthException(String message) {
        super(Code.ACCESS_DENIED,message);
        log.error("접근 불가");
    }
}
