package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.ArchiveDTO;
import sequence.sequence_member.archive.entity.ArchiveEntity;
import sequence.sequence_member.archive.repository.ArchiveRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArchiveService {
    private final ArchiveRepository archiveRepository;

    // Create 생성
    @Transactional
    public ArchiveDTO saveArchive(ArchiveDTO archiveDTO) {
        ArchiveEntity archiveEntity = ArchiveEntity.builder()
                .title(archiveDTO.getTitle())
                .description(archiveDTO.getDescription())
                .duration(archiveDTO.getDuration())
                .field(archiveDTO.getField())
                .status(archiveDTO.getStatus()) // byte 값 그대로 저장
                .build();

        ArchiveEntity saveArchive = archiveRepository.save(archiveEntity);

        return ArchiveDTO.builder()
                .archiveId(saveArchive.getArchiveId())
                .title(saveArchive.getTitle())
                .description(saveArchive.getDescription())
                .duration(saveArchive.getDuration())
                .field(saveArchive.getField())
                .status(saveArchive.getStatus())
                .build();
    }
    
    // Read 조회
    public ArchiveDTO getArchiveById(Long archiveId) {
        Optional<ArchiveEntity> archiveEntityOpt = archiveRepository.findById(archiveId);

        if (archiveEntityOpt.isEmpty()) {
            throw new RuntimeException("Archive not found");
        }

        ArchiveEntity archiveEntity = archiveEntityOpt.get();

        return ArchiveDTO.builder()
                .archiveId(archiveEntity.getArchiveId())
                .title(archiveEntity.getTitle())
                .description(archiveEntity.getDescription())
                .duration(archiveEntity.getDuration())
                .field(archiveEntity.getField())
                .status(archiveEntity.getStatus())
                .build();
    }

    // 모든 아카이브 Read 조회
    public List<ArchiveDTO> getAllArchives() {
        List<ArchiveEntity> archiveEntities = archiveRepository.findAll();

        return archiveEntities.stream()
                .map(archiveEntity -> ArchiveDTO.builder()
                        .archiveId(archiveEntity.getArchiveId())
                        .title(archiveEntity.getTitle())
                        .description(archiveEntity.getDescription())
                        .duration(archiveEntity.getDuration())
                        .field(archiveEntity.getField())
                        .status(archiveEntity.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
