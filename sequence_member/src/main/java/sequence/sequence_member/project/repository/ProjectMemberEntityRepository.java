package sequence.sequence_member.project.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectMember;

@Repository
public interface ProjectMemberEntityRepository extends JpaRepository<ProjectMember,Long > {

    public void deleteAllByProjectId(Long projectId);

    public void deleteByProjectAndMemberIn(Project project, Collection<MemberEntity> members);
}
