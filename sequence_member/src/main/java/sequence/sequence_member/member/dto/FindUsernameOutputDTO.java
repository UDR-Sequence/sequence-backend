package sequence.sequence_member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FindUsernameOutputDTO {
    private String name;
    private String username;
    private LocalDate created_date_time;
}
