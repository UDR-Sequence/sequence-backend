package sequence.sequence_member.archive.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.archive.dto.ArchiveDTO;
import sequence.sequence_member.archive.entity.ArchiveEntity;
import sequence.sequence_member.archive.repository.ArchiveRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;

    // 모든 아카이브 조회
    public List<ArchiveDTO> getAllArchives() {
        List<ArchiveEntity> archives = archiveRepository.findAll();
        return archives.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 상태(등록 완료 or 미등록) 아카이브 조회
    public List<ArchiveDTO> getArchivesByStatus(Byte status) {
        List<ArchiveEntity> archives = archiveRepository.findByStatus(status);
        return archives.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 아카이브 등록
    @Transactional
    public ArchiveDTO createArchive(ArchiveDTO archiveDTO) {
        ArchiveEntity archive = convertToEntity(archiveDTO);
        ArchiveEntity savedArchive = archiveRepository.save(archive);
        return convertToDto(savedArchive);
    }

    // DTO → Entity 변환
    private ArchiveEntity convertToEntity(ArchiveDTO dto) {
        return ArchiveEntity.builder()
                .archiveId(dto.getArchiveId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .field(dto.getField())
                .status(dto.getStatus())
                .build();
    }

    // Entity → DTO 변환
    private ArchiveDTO convertToDto(ArchiveEntity entity) {
        return ArchiveDTO.builder()
                .archiveId(entity.getArchiveId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .field(entity.getField())
                .status(entity.getStatus())
                .build();
    }
}
