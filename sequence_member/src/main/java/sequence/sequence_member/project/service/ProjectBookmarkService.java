package sequence.sequence_member.project.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.ProjectSummaryInfoDTO;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectBookmark;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectBookmarkService {
    private final ProjectBookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public String addBookmark(CustomUserDetails customUserDetails, Long projectId) {
        StringBuilder errMessgage=new StringBuilder(); //만약 null이 아닐경우 오류가 발생한것
        // 유저와 프로젝트 존재 여부 확인
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElse(null);
        Project project = projectRepository.findById(projectId).orElse(null);

        if(member == null){
            errMessgage.append("멤버를 찾을 수 없습니다.\n");
        }
        if(project == null){
            errMessgage.append("프로젝트를 찾을 수 없습니다.\n");
        }
        if(!errMessgage.isEmpty()){
            throw new CanNotFindResourceException(errMessgage.toString());
        }

        // 이미 북마크한 경우 무시
        if (bookmarkRepository.existsByMemberIdAndProjectId(member.getId(), projectId)) {
            return "이미 등록한 북마크입니다.";
        }

        // 북마크 저장
        ProjectBookmark bookmark = ProjectBookmark.builder()
                .member(member)
                .project(project)
                .build();

        bookmarkRepository.save(bookmark);
        return "북마크 등록 성공";
    }

    @Transactional
    public String removeBookmark(CustomUserDetails customUserDetails, Long projectId) {
        //todo- 코드 반복됨. 리팩토링 고민
        StringBuilder errMessgage=new StringBuilder(); //만약 null이 아닐경우 오류가 발생한것
        // 유저와 프로젝트 존재 여부 확인
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElse(null);
        Project project = projectRepository.findById(projectId).orElse(null);

        if(member == null){
            errMessgage.append("멤버를 찾을 수 없습니다.\n");
        }
        if(project == null){
            errMessgage.append("프로젝트를 찾을 수 없습니다.\n");
        }
        if(!errMessgage.isEmpty()){
            throw new CanNotFindResourceException(errMessgage.toString());
        }

        // 북마크가 존재하는지 확인
        if (!bookmarkRepository.existsByMemberIdAndProjectId(member.getId(), projectId)) {
            return "해당 프로젝트는 북마크되지 않았습니다.";
        }

        // 북마크 삭제
        bookmarkRepository.deleteByMemberIdAndProjectId(member.getId(), projectId);
        return "북마크 삭제 성공";
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryInfoDTO> getBookmarkedProjects(CustomUserDetails customUserDetails) {
        StringBuilder errMessgage=new StringBuilder(); //만약 null이 아닐경우 오류가 발생한것
        // 유저와 프로젝트 존재 여부 확인
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElse(null);

        if(member == null){
            errMessgage.append("멤버를 찾을 수 없습니다.\n");
        }

        // 북마크한 프로젝트 목록 조회
        List<Project> bookmarkedProjects = bookmarkRepository.findBookmarkedProjectsByMemberId(member.getId());

        // DTO 변환 후 반환
        return bookmarkedProjects.stream()
                .map(project -> ProjectSummaryInfoDTO.builder()
                        .projectId(project.getId())
                        .projectTitle(project.getTitle())
                        .projectName(project.getProjectName())
                        .projectWriterNickname(project.getWriter().getNickname())
                        .category(project.getCategory())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
