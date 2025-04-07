package sequence.sequence_member.member.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 초대된 프로젝트 목록 조회시 사용
@Getter
@Builder
public class InviteProjectOutputDTO {
    private final Long projectInvitedMemberId; // 초대된 초대장 id
    private final String writer; // 작성자_닉네임
    private final String title; // 제목
    private final LocalDate inviteDate; // 초대일
}
