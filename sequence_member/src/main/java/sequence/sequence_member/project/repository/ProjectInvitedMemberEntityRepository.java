package sequence.sequence_member.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.project.entity.ProjectInvitedMember;

@Repository
public interface ProjectInvitedMemberEntityRepository extends JpaRepository<ProjectInvitedMember,Long> {
}
