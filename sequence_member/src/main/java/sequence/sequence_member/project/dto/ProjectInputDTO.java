package sequence.sequence_member.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.MeetingOption;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Step;

@Getter
public class ProjectInputDTO {

    @NotEmpty(message = "제목을 입력해주세요.")
    @Length(min=1, max=40,message = "글 제목은 30자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "제목을 입력해주세요.")
    @Length(min=1, max=40,message = "프로젝트 제목은 30자 이하로 입력해주세요.")
    private String projectName; //프로젝트 이름

    @NotNull(message = "기간을 선택해주세요.")
    private Period period;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @NotNull(message = "모집 인원을 입력해주세요.")
    @Min(value = 1, message = "모집 인원은 1명 이상이어야 합니다.")
    private Integer personnel;

    @NotEmpty(message = "모집 하고자 하는 역할들을 선택해주세요")
    @Size(max = 20, message = "역할들은 20개 이하로 선택 해주세요.")
    private List<String> roles;

    @NotEmpty(message = "필요로 하는 스킬들을 선택해주세요")
    @Size(max = 20, message = "스킬들은 20개 이하로 선택 해주세요.")
    private List<String> skills;

    @NotNull(message = "진행 방식을 선택 해주세요")
    private MeetingOption meetingOption;

    @NotNull(message = "현재 진행 단계를 선택 해주세요.")
    private Step step;

    private List<String> invitedMembersNicknames; // 닉네임 리스트

    @Length(min=1,max = 450, message = "소개글은 1자이상 450자 이하여야합니다.")
    private String introduce;

    @Length(min=1,max = 450, message = "모집글은 1자이상 450자 이하여야합니다.")
    private String article;

    private String link;
}
