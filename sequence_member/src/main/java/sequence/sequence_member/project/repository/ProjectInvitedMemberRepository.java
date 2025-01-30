package sequence.sequence_member.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.project.entity.ProjectInvitedMember;

@Repository
public interface ProjectInvitedMemberRepository extends JpaRepository<ProjectInvitedMember,Long> {
    public List<ProjectInvitedMember> findByMemberId(Long memberId);
}
