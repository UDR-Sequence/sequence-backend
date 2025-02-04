package sequence.sequence_member.archive.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.archive.dto.ArchiveMemberDTO;
import sequence.sequence_member.archive.entity.ArchiveMemberEntity;
import sequence.sequence_member.archive.repository.ArchiveMemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArchiveMemberService {

    private final ArchiveMemberRepository archiveMemberRepository;

    // 특정 아카이브에 속한 팀원 조회
    public List<ArchiveMemberDTO> getMembersByArchive(Long archiveId) {
        List<ArchiveMemberEntity> members = archiveMemberRepository.findByArchiveId_ArchiveId(archiveId);
        return members.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 멤버가 속한 아카이브 조회
    public List<ArchiveMemberDTO> getArchivesByMember(Long memberId) {
        List<ArchiveMemberEntity> archives = archiveMemberRepository.findByMemberId_MemberId(memberId);
        return archives.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // DTO → Entity 변환
    private ArchiveMemberEntity convertToEntity(ArchiveMemberDTO dto) {
        return ArchiveMemberEntity.builder()
                .archiveMemberId(dto.getArchiveMemberId())
                .memberId(dto.getMemberId().toEntity()) // MemberDTO → MemberEntity 변환 필요
                .archiveId(dto.getArchiveId().toEntity()) // ArchiveDTO → ArchiveEntity 변환 필요
                .build();
    }

    // Entity → DTO 변환
    private ArchiveMemberDTO convertToDto(ArchiveMemberEntity entity) {
        return ArchiveMemberDTO.builder()
                .archiveMemberId(entity.getArchiveMemberId())
                .memberId(entity.getMemberId().toDto()) // MemberEntity → MemberDTO 변환 필요
                .archiveId(entity.getArchiveId().toDto()) // ArchiveEntity → ArchiveDTO 변환 필요
                .build();
    }
}
