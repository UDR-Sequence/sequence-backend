package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.ArchiveOutputDTO;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.dto.ArchiveRegisterInputDTO;
import sequence.sequence_member.archive.dto.ArchiveUpdateDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.global.exception.CanNotFindResourceException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {
    
    private final ArchiveRepository archiveRepository;

    @Transactional
    public ArchiveOutputDTO createArchive(ArchiveRegisterInputDTO archiveRegisterInputDTO) {
        Archive archive = Archive.builder()
                .title(archiveRegisterInputDTO.getTitle())
                .description(archiveRegisterInputDTO.getDescription())
                .duration(archiveRegisterInputDTO.getDuration())
                .category(archiveRegisterInputDTO.getCategory())
                .period(archiveRegisterInputDTO.getPeriod())
                .status(archiveRegisterInputDTO.getStatus())
                .thumbnail(archiveRegisterInputDTO.getThumbnail())
                .link(archiveRegisterInputDTO.getLink())
                .build();

        archive.setSkillsFromList(archiveRegisterInputDTO.getSkills());

        Archive savedArchive = archiveRepository.save(archive);

        return ArchiveOutputDTO.builder()
                .id(savedArchive.getId())
                .title(savedArchive.getTitle())
                .description(savedArchive.getDescription())
                .duration(savedArchive.getDuration())
                .category(savedArchive.getCategory())
                .period(savedArchive.getPeriod())
                .status(savedArchive.getStatus())
                .thumbnail(savedArchive.getThumbnail())
                .link(savedArchive.getLink())
                .skills(savedArchive.getSkillList())
                .view(savedArchive.getView())
                .bookmark(savedArchive.getBookmark())
                .createdDateTime(savedArchive.getCreatedDateTime())
                .modifiedDateTime(savedArchive.getModifiedDateTime())
                .build();
    }

    // 아카이브 등록 후 결과 조회
    public ArchiveOutputDTO getArchiveById(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아카이브가 없습니다."));
        return ArchiveOutputDTO.builder()
                .id(archive.getId())
                .title(archive.getTitle())
                .description(archive.getDescription())
                .duration(archive.getDuration())
                .category(archive.getCategory())
                .period(archive.getPeriod())
                .status(archive.getStatus())
                .thumbnail(archive.getThumbnail())
                .link(archive.getLink())
                .skills(archive.getSkillList())
                .view(archive.getView())
                .bookmark(archive.getBookmark())
                .createdDateTime(archive.getCreatedDateTime())
                .modifiedDateTime(archive.getModifiedDateTime())
                .build();
    }

    // 아카이브 내용 수정
    @Transactional
    public void updateArchive(Long archiveId, ArchiveUpdateDTO archiveUpdateDTO) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(()-> new IllegalArgumentException("해당 아카이브가 없습니다."));
        archive.updateArchive(archiveUpdateDTO);
    }

    // 아카이브 삭제
    @Transactional
    public void deleteArchive(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(()-> new IllegalArgumentException("해당 아카이브가 없습니다."));
        archiveRepository.delete(archive);
    }

    
    // 전체 아카이브 목록 조회 (정렬 추가)
    public ArchivePageResponseDTO getAllArchives(int page, SortType sortType) {
        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage = archiveRepository.findAll(pageable);
        
        if(archivePage.isEmpty()) {
            throw new CanNotFindResourceException("조건에 맞는 프로젝트를 찾을 수 없습니다.");
        }
        
        return createArchivePageResponse(archivePage);
    }
    
    // 카테고리별 아카이브 검색 (정렬 추가)
    public ArchivePageResponseDTO searchByCategory(Category category, int page, SortType sortType) {
        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage = archiveRepository.findByCategory(category, pageable);
        
        if(archivePage.isEmpty()) {
            throw new CanNotFindResourceException("조건에 맞는 프로젝트를 찾을 수 없습니다.");
        }
        
        return createArchivePageResponse(archivePage);
    }
    /* */
    // 제목으로 아카이브 검색 (정렬 추가)
    public ArchivePageResponseDTO searchByTitle(String keyword, int page, SortType sortType) {
        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage = archiveRepository.findByTitleContaining(keyword, pageable);
        
        if(archivePage.isEmpty()) {
            throw new CanNotFindResourceException("조건에 맞는 프로젝트를 찾을 수 없습니다.");
        }
        
        return createArchivePageResponse(archivePage);
    }

    // 정렬 조건이 포함된 Pageable 객체 생성
    private Pageable createPageableWithSort(int page, SortType sortType) {
        Sort sort = switch (sortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdDateTime");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdDateTime");
            case MOST_VIEWED -> Sort.by(Sort.Direction.DESC, "view");
            case MOST_BOOKMARKED -> Sort.by(Sort.Direction.DESC, "bookmark");
            default -> Sort.by(Sort.Direction.DESC, "createdDateTime");
        };
        
        return PageRequest.of(page, 18, sort);
    }

    // Archive 엔티티를 DTO로 변환
    private ArchiveOutputDTO convertToDTO(Archive archive) {
        return ArchiveOutputDTO.builder()
                .id(archive.getId())
                .title(archive.getTitle())
                .description(archive.getDescription())
                .duration(archive.getDuration())
                .category(archive.getCategory())
                .period(archive.getPeriod())
                .status(archive.getStatus()) 
                .thumbnail(archive.getThumbnail())
                .link(archive.getLink())
                .skills(archive.getSkillList())
                .imgUrls(archive.getImageUrlsAsList())
                .view(archive.getView())
                .bookmark(archive.getBookmark())
                .createdDateTime(archive.getCreatedDateTime())
                .modifiedDateTime(archive.getModifiedDateTime())
                .build();
    }

    private ArchivePageResponseDTO createArchivePageResponse(Page<Archive> archivePage) {
        return ArchivePageResponseDTO.builder()
                .archives(archivePage.getContent().stream()
                        .map(this::convertToDTO)
                        .toList())
                .totalPages(archivePage.getTotalPages())
                .build();
    }
} 