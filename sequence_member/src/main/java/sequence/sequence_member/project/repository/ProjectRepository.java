package sequence.sequence_member.project.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.MeetingOption;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Step;
import sequence.sequence_member.project.dto.ProjectOutputDTO;
import sequence.sequence_member.project.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    public Optional<Project> getProjectEntityById(Long projectId);

    //키워드 필터링
    @Query("SELECT p FROM Project p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:period IS NULL OR p.period = :period) AND " +
            "(:roles IS NULL OR p.roles LIKE CONCAT('%', :roles , '%')) AND " +
            "(:skills IS NULL OR p.skills LIKE CONCAT('%', :skills, '%')) AND " +
            "(:meeting_option IS NULL OR p.meetingOption = :meeting_option) AND " +
            "(:step IS NULL OR p.step = :step)"
    )
    List<Project> findProjectsByFilteredKeywords(
            @Param("category") Category category,
            @Param("period") Period period,
            @Param("roles") String roles,
            @Param("skills") String skills,
            @Param("meeting_option") MeetingOption meeting_option,
            @Param("step") Step step
    );

    //검색 필터링
    @Query("SELECT q From Project q WHERE " + "(q.title LIKE CONCAT('%', :title, '%'))")
    List<Project> findProjectsByFilterdSearch(@Param("title") String title);

    @Query("SELECT p From Project p")
    List<Project> findAllProjects();
}