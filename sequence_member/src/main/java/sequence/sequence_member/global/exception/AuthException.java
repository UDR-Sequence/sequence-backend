package sequence.sequence_member.global.exception;

import sequence.sequence_member.global.response.Code;

public class AuthException extends BaseException {
    public AuthException(String message) {
        super(Code.ACCESS_DENIED,message);
    }
}
