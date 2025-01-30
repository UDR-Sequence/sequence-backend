package sequence.sequence_member.project.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectMember;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long > {

    public void deleteAllByProjectId(Long projectId);

    public void deleteByProjectAndMemberIn(Project project, Collection<MemberEntity> members);

    // 멤버와 프로젝트 아이디로 프로젝트 멤버를 찾는 함수
    public ProjectMember findByMemberIdAndProjectId(Long memberId, Long projectId);

    // 멤버가 모든 프로젝트를 찾는 함수
    public List<ProjectMember> findByMemberId(Long memberId);
}
