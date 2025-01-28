package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.ArchiveDTO;
import sequence.sequence_member.archive.entity.ArchiveEntity;
import sequence.sequence_member.archive.repository.ArchiveRepository;

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
    
}
