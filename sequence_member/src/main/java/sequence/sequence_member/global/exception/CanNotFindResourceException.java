package sequence.sequence_member.global.exception;

import sequence.sequence_member.global.response.Code;

public class CanNotFindResourceException extends BaseException {
    public CanNotFindResourceException(String message) {
        super(Code.CAN_NOT_FIND_RESOURCE,message);
    }
}
