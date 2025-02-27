package sequence.sequence_member.global.exception;


import sequence.sequence_member.global.response.Code;

public class MultipartException extends BaseException {
    public MultipartException() {
        super(Code.INVALID_FILE_EXTENSION);
    }
}