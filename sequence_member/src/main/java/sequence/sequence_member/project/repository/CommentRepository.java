package sequence.sequence_member.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.project.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.project.id = :projectId")
    int countByProjectId(Long projectId);
}