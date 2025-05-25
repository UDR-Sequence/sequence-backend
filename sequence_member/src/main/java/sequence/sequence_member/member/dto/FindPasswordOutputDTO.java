package sequence.sequence_member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordOutputDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> FindPasswordOutputDTO<T> success(String message, T data) {
        return FindPasswordOutputDTO.<T>builder().success(true)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> FindPasswordOutputDTO<T> fail(String message) {
        return FindPasswordOutputDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
