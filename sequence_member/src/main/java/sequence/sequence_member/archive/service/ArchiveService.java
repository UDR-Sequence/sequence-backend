package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    //전체 아카이브 조회
    

}
