package sequence.sequence_member.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.project.entity.ProjectBookmark;

@Repository
public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Long> {
    boolean existsByMemberIdAndProjectId(Long memberId, Long projectId); // 북마크 존재 여부 확인

    void deleteByMemberIdAndProjectId(Long memberId, Long projectId); // 북마크 삭제
}
