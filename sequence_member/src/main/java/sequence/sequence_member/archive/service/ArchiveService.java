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
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.archive.entity.ArchiveMember;
import sequence.sequence_member.archive.repository.ArchiveMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sequence.sequence_member.global.enums.enums.Status;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {
    
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;
    private final ArchiveMemberRepository archiveMemberRepository;

    @Transactional
    public ArchiveOutputDTO createArchive(ArchiveRegisterInputDTO dto) {
        Archive archive = Archive.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .category(dto.getCategory())
                .period(dto.getPeriod())
                .status(Status.평가전)
                .thumbnail(dto.getThumbnail())
                .link(dto.getLink())
                .build();

        archive.setSkillsFromList(dto.getSkills());
        Archive savedArchive = archiveRepository.save(archive);

        // 아카이브 멤버 등록
        for (ArchiveRegisterInputDTO.ArchiveMemberDTO memberDto : dto.getArchiveMembers()) {
            MemberEntity member = memberRepository.findByUsername(memberDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다: " + memberDto.getUsername()));

            ArchiveMember archiveMember = ArchiveMember.builder()
                .archive(savedArchive)
                .member(member)
                .role(memberDto.getRole())
                .build();

            archiveMemberRepository.save(archiveMember);
        }

        return convertToDTO(savedArchive);
    }

    // 아카이브 등록 후 결과 조회
    public ArchiveOutputDTO getArchiveById(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아카이브가 없습니다."));
        
        return convertToDTO(archive);
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
        List<ArchiveOutputDTO.ArchiveMemberDTO> memberDTOs = archive.getArchiveMembers().stream()
            .map(archiveMember -> ArchiveOutputDTO.ArchiveMemberDTO.builder()
                .username(archiveMember.getMember().getUsername())
                .nickname(archiveMember.getMember().getNickname())
                .role(archiveMember.getRole())
                .build())
            .collect(Collectors.toList());

        return ArchiveOutputDTO.builder()
                .id(archive.getId())
                .title(archive.getTitle())
                .description(archive.getDescription())
                .duration(archive.getDuration())
                .category(archive.getCategory())
                .period(archive.getPeriod())
                .thumbnail(archive.getThumbnail())
                .link(archive.getLink())
                .skills(archive.getSkillList())
                .imgUrls(archive.getImageUrlsAsList())
                .view(archive.getView())
                .bookmark(archive.getBookmark())
                .members(memberDTOs)
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