package sequence.sequence_member.member.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 참여하고있는 프로젝트 목록 조회
@Getter
@Builder
public class AcceptProjectOutputDTO {

    private final Long projectId;
    private final String writer; // 작성자_닉네임
    private final String title; // 제목
    private final LocalDate createdDate; // 작성일
}
