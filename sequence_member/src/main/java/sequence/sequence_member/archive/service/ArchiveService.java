package sequence.sequence_member.archive.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
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
        if (archiveDTO.getTitle() == null || archiveDTO.getTitle().isEmpty()) {
            throw new ValidationException("제목은 필수 입력값입니다.");
        }
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
    
    // Read 단일 조회
    public ArchiveDTO getArchiveById(Long archiveId) {
        ArchiveEntity archiveEntity = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 아카이브가 존재하지 않습니다: " + archiveId));

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

    // Update
    @Transactional
    public ArchiveDTO updateArchive(Long archiveId, ArchiveDTO archiveDTO) {
        ArchiveEntity archiveEntity = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 아카이브가 존재하지 않습니다:" + archiveId));

        archiveEntity.setTitle(archiveDTO.getTitle());
        archiveEntity.setDescription(archiveDTO.getDescription());
        archiveEntity.setDuration(archiveDTO.getDuration());
        archiveEntity.setField(archiveDTO.getField());
        archiveEntity.setStatus(archiveDTO.getStatus());

        ArchiveEntity updateArchiveEntity = archiveRepository.save(archiveEntity);

        return ArchiveDTO.builder()
                .archiveId(updateArchiveEntity.getArchiveId())
                .title(updateArchiveEntity.getTitle())
                .description(updateArchiveEntity.getDescription())
                .duration(updateArchiveEntity.getDuration())
                .field(updateArchiveEntity.getField())
                .status(updateArchiveEntity.getStatus())
                .build();
    }

    // Delete
    @Transactional
    public void deleteArchive(Long archiveId) {
       if (!archiveRepository.existsById(archiveId)) {
           throw new EntityNotFoundException("해당 ID의 아카이브가 존재하지 않아 삭제할 수 없습니다: " + archiveId);
       }
       archiveRepository.deleteById(archiveId);
    }
}
