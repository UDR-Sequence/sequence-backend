package sequence.sequence_member.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.project.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    public Optional<Project> getProjectEntityById(Long projectId);
}