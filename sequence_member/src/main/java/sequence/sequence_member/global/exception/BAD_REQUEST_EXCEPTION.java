package sequence.sequence_member.global.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BAD_REQUEST_EXCEPTION extends RuntimeException {
    public BAD_REQUEST_EXCEPTION(String message) {
        super(message);
        log.error("Bad Request error");
    }
}