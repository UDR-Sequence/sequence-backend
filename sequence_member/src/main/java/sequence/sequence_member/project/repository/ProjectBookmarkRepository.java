package sequence.sequence_member.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectBookmark;

@Repository
public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long> {
    boolean existsByMemberIdAndProjectId(Long memberId, Long projectId); // 북마크 존재 여부 확인

    void deleteByMemberIdAndProjectId(Long memberId, Long projectId); // 북마크 삭제

    // ✅ 유저가 북마크한 프로젝트 목록 조회
    @Query("SELECT pb.project FROM ProjectBookmark pb WHERE pb.member.id = :memberId")
    Page<Project> findBookmarkedProjectsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 특정 사용자가 북마크한 Project 게시글을 조회 (member의 id로 조회)
    Page<ProjectBookmark> findByMember_Id(Long memberId, Pageable pageable);

    List<ProjectBookmark> findAllByMember(MemberEntity member, Sort sort);

    List<ProjectBookmark> findAllByProject(Project project);
}
