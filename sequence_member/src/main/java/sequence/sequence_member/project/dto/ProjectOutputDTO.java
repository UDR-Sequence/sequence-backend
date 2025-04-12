package sequence.sequence_member.project.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.MeetingOption;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Step;
import sequence.sequence_member.global.utils.DataConvertor;
import sequence.sequence_member.project.entity.Project;

@Getter @Builder
public class ProjectOutputDTO {

    private Long id; // 프로젝트 id
    private String title; // 글 제목
    private String writer; // 프로젝트 작성자 닉메임
    private LocalDate createdDate; // 프로젝트 작성일
    private String projectName; // 프로젝트 이름
    private Period period; // 프로젝트 기간
    private String startDate;
    private String endDate;
    private Category category; // 카테고리
    private Integer personnel; // 모집 인원
    private List<String> roles; // 역할
    private List<String> skills; // 스킬
    private MeetingOption meetingOption; // 모임 방식
    private Step step; // 프로젝트 진행 단계
    private String introduce; // 소개글
    private String article; // 모집글
    private String link; // 링크
    private List<ProjectMemberOutputDTO> members; // 초대된 멤버들 nickname, 프로밀이미지, 멤버ID 값을 가지고있음.
    private List<CommentOutputDTO> comments; // 댓글들
    private Integer views; //조회수
    private Boolean isBookmark; //북마크 여부
    private Integer bookmarks; // 북마크 수

    public static ProjectOutputDTO from(Project project, List<ProjectMemberOutputDTO> members, List<CommentOutputDTO> comments, int views, boolean isBookmark) {
        return ProjectOutputDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .writer(project.getWriter().getNickname())
                .createdDate(java.sql.Date.valueOf(project.getCreatedDateTime().toLocalDate()))
                .projectName(project.getProjectName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .period(project.getPeriod())
                .category(project.getCategory())
                .personnel(project.getPersonnel())
                .roles(DataConvertor.stringToList(project.getRoles()))
                .skills(DataConvertor.stringToList(project.getSkills()))
                .meetingOption(project.getMeetingOption())
                .step(project.getStep())
                .introduce(project.getIntroduce())
                .article(project.getArticle())
                .link(project.getLink())
                .members(members)
                .comments(comments)
                .views(views)
                .isBookmark(isBookmark)
                .bookmarks(project.getBookmarkCount())
                .build();
    }
}
