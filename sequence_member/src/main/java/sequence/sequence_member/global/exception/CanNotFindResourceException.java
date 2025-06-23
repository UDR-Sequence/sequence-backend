package sequence.sequence_member.global.exception;

import lombok.extern.slf4j.Slf4j;
import sequence.sequence_member.global.response.Code;

@Slf4j
public class CanNotFindResourceException extends BaseException {
    public CanNotFindResourceException(String message) {
        super(Code.CAN_NOT_FIND_RESOURCE,message);
        log.error("해당 리소스 발견 불가");
    }
}
