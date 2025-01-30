package sequence.sequence_member.project.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.project.entity.ProjectInvitedMember;

@Repository
public interface ProjectInvitedMemberRepository extends JpaRepository<ProjectInvitedMember,Long> {
    public List<ProjectInvitedMember> findByMemberId(Long memberId);
    public Optional<ProjectInvitedMember> findByIdAndMemberId(Long memberId, Long projectId);
}
