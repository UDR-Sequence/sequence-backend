package sequence.sequence_member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordOutputDTO {
    private boolean success;
    private String message;

    public static FindPasswordOutputDTO success(String message) {
        return FindPasswordOutputDTO.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static FindPasswordOutputDTO fail(String message) {
        return FindPasswordOutputDTO.builder()
                .success(false)
                .message(message)
                .build();
    }
}
