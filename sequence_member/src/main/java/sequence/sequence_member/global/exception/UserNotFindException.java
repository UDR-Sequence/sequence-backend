package sequence.sequence_member.global.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFindException extends RuntimeException {
    public UserNotFindException(String message) {
        super(message);
        log.error("해당 유저를 찾을 수 없습니다.");
    }
}
