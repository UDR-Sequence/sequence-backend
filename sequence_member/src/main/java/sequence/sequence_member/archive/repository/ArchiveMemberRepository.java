package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.ArchiveMember;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.member.entity.MemberEntity;
import java.util.List;

public interface ArchiveMemberRepository extends JpaRepository<ArchiveMember, Long> {
    // 기본적인 CRUD 메서드는 JpaRepository에서 제공됨

    // 아카이브와 멤버 ID로 아카이브 멤버 찾기
    ArchiveMember findByArchiveAndMemberId(Archive archive, Long memberId);

    // 멤버와 아카이브 ID로 아카이브 멤버 찾기
    ArchiveMember findByMemberAndArchive_Id(MemberEntity member, Long archiveId);

    List<ArchiveMember> findAllByArchiveId(Long archiveId);
} 