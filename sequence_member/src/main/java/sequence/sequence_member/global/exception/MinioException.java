package sequence.sequence_member.global.exception;

import static sequence.sequence_member.global.response.Code.INTERNAL_SERVER_MINIO_ERROR;

public class MinioException extends BaseException {

    public MinioException() {
        super(INTERNAL_SERVER_MINIO_ERROR);
    }

    public MinioException(String message) {
        super(INTERNAL_SERVER_MINIO_ERROR, message);
    }
}