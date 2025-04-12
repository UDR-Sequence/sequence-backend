package sequence.sequence_member.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class InvitedProjectWithCommentDTO {
    private final Long projectInvitedMemberId; // 초대된 초대장 id
    private final String writer; // 작성자_닉네임
    private final String title; // 제목
    private final LocalDate inviteDate; // 초대일
    private int commentCount;   // 댓글수
}
