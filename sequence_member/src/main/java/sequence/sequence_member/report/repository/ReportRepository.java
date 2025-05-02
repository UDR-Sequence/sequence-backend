package sequence.sequence_member.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.report.entity.ReportEntity;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByNickname(String nickname);
}
