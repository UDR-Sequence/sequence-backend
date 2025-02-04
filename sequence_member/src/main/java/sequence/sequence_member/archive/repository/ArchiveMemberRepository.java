package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.ArchiveMemberEntity;
import java.util.List;

public interface ArchiveMemberRepository extends JpaRepository<ArchiveMemberEntity, Long> {
    // 특정 아카이브에 속한 팀원 조회
    List<ArchiveMemberEntity> findByArchiveId_ArchiveId(Long archiveId);

    // 특정 멤버가 속한 아카이브 조회
    List<ArchiveMemberEntity> findByMemberId_MemberId(Long memberId);
}
