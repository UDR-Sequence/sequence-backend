package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.entity.PortfolioEntity;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
    @Query("SELECT a FROM PortfolioEntity a WHERE a.member = :member AND a.isDeleted = false")
    List<PortfolioEntity> findByMemberAndIsDeletedFalse(MemberEntity member);

}
