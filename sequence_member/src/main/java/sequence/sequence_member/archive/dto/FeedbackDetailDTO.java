package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Period;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDetailDTO {
    private String content;
    private Period duration;
}
