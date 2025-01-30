package sequence.sequence_member.member.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

// 초대된 프로젝트 목록 조회시 사용
@Getter
@Builder
public class InviteOutputDTO {

    private final Long projectId;
    private final String writer; // 작성자_닉네임
    private final String title; // 제목
    private final Date inviteDate; // 초대일

}
