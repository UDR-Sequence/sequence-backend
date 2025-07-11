package sequence.sequence_member.project.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElse(null);
        Project project = projectRepository.findById(projectId).orElse(null);

        if(member == null){
            errMessgage.append("멤버를 찾을 수 없습니다.\n");
            log.error("멤버를 찾을 수 없음");
        }
        if(project == null){
            errMessgage.append("프로젝트를 찾을 수 없습니다.\n");
            log.error("프로젝트를 찾을 수 없음");
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
        project.addBookmarkCount();
        projectRepository.save(project);
        return "북마크 등록 성공";
    }

    @Transactional
    public String removeBookmark(CustomUserDetails customUserDetails, Long projectId) {
        //todo- 코드 반복됨. 리팩토링 고민
        StringBuilder errMessgage=new StringBuilder(); //만약 null이 아닐경우 오류가 발생한것
        // 유저와 프로젝트 존재 여부 확인
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElse(null);
        Project project = projectRepository.findById(projectId).orElse(null);

        if(member == null){
            errMessgage.append("멤버를 찾을 수 없습니다.\n");
            log.error("멤버를 찾을 수 없음");
        }
        if(project == null){
            errMessgage.append("프로젝트를 찾을 수 없습니다.\n");
            log.error("프로젝트를 찾을 수 없음");
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
        project.removeBookmarkCount();
        projectRepository.save(project);
        return "북마크 삭제 성공";
    }

    @Transactional
    public void deleteByProject(Project project, String username){
        List<ProjectBookmark> bookmarks = bookmarkRepository.findAllByProject(project);
        if (bookmarks != null && !bookmarks.isEmpty()) {
            for (ProjectBookmark bookmark : bookmarks) {
                bookmark.softDelete(username);
            }
            bookmarkRepository.saveAll(bookmarks);
        }
    }

    // 프로젝트 북마크 여부 확인 ( 북마크 되어있으면 true, 아니면 false, 로그인 안한 사용자는 false)
    public boolean isBookmarked(Long projectId, CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            log.error("로그인 재시도 필요 : No customUserDetail here");
            return false;
        }
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElse(null);
        if (member == null) {
            log.error("멤버를 찾을 수 없음");
            return false;
        }
        return bookmarkRepository.existsByMemberIdAndProjectId(member.getId(), projectId);
    }

}
