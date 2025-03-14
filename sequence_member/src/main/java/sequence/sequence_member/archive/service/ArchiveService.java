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
import sequence.sequence_member.archive.dto.ArchiveMemberDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.archive.entity.ArchiveMember;
import sequence.sequence_member.archive.repository.ArchiveMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sequence.sequence_member.global.enums.enums.Status;
import java.util.List;
import java.util.stream.Collectors;
import sequence.sequence_member.archive.repository.ArchiveBookmarkRepository;
import sequence.sequence_member.archive.repository.ArchiveCommentRepository;
import java.util.ArrayList;
import sequence.sequence_member.archive.dto.ArchiveCommentOutputDTO;
import sequence.sequence_member.archive.entity.ArchiveComment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.repository.query.Param;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {
    
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;
    private final ArchiveMemberRepository archiveMemberRepository;
    private final ArchiveBookmarkRepository bookmarkRepository;
    private final ArchiveCommentRepository commentRepository;
    private final ArchiveViewService archiveViewService;

    @Transactional
    public Long createArchive(ArchiveRegisterInputDTO dto, String username) {
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        Archive archive = Archive.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .category(dto.getCategory())
                .status(Status.평가전)
                .thumbnail(dto.getThumbnail())
                .link(dto.getLink())
                .build();

        archive.setSkillsFromList(dto.getSkills());
        Archive savedArchive = archiveRepository.save(archive);

        // 아카이브 멤버 등록
        for (ArchiveMemberDTO memberDto : dto.getArchiveMembers()) {
            MemberEntity archiveMember = memberRepository.findByUsername(memberDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다: " + memberDto.getUsername()));

            ArchiveMember newArchiveMember = ArchiveMember.builder()
                .archive(savedArchive)
                .member(archiveMember)
                .build();

            archiveMemberRepository.save(newArchiveMember);
        }

        return savedArchive.getId();  // 아카이브 ID 반환
    }

    public ArchiveOutputDTO getArchiveById(Long archiveId, String username, HttpServletRequest request) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 아카이브가 없습니다."));
        
        // Redis에서 조회수 처리
        int viewCount = archiveViewService.getViewsFromRedis(request, archiveId);
        
        return convertToDTO(archive, username, viewCount);
    }

    @Transactional
    public void updateArchive(Long archiveId, ArchiveUpdateDTO archiveUpdateDTO, String username) {
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        // 아카이브 멤버 검증
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 아카이브의 멤버가 아닙니다.");
        }

        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 아카이브가 없습니다."));
        archive.updateArchive(archiveUpdateDTO);
    }

    @Transactional
    public void deleteArchive(Long archiveId, String username) {
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        // 아카이브 멤버 검증
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 아카이브의 멤버가 아닙니다.");
        }

        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 아카이브가 없습니다."));
        archiveRepository.delete(archive);
    }

    public ArchivePageResponseDTO getAllArchives(int page, SortType sortType, String username) {
        // username이 null이 아닐 때만 사용자 검증
        if (username != null) {
            memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));
        }

        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage = archiveRepository.findAll(pageable);
        
        if(archivePage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "조건에 맞는 프로젝트를 찾을 수 없습니다.");
        }
        
        return createArchivePageResponse(archivePage, username);
    }

    public ArchivePageResponseDTO searchArchives(
            Category category, 
            String keyword, 
            int page, 
            SortType sortType, 
            String username) {
        
        // username이 null이 아닐 때만 사용자 검증
        if (username != null) {
            memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));
        }

        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage;
        
        // null 체크를 통한 메서드 선택
        if (category != null && keyword != null && !keyword.trim().isEmpty()) {
            archivePage = archiveRepository.findByCategoryAndTitleContainingIgnoreCase(category, keyword.trim(), pageable);
        } else if (category != null) {
            archivePage = archiveRepository.findByCategory(category, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            archivePage = archiveRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            archivePage = archiveRepository.findAll(pageable);
        }

        if(archivePage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "조건에 맞는 프로젝트를 찾을 수 없습니다.");
        }
        
        return createArchivePageResponse(archivePage, username);
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
    private ArchiveOutputDTO convertToDTO(Archive archive, String username, int viewCount) {
        List<ArchiveOutputDTO.ArchiveMemberDTO> memberDTOs = archive.getArchiveMembers().stream()
            .map(archiveMember -> ArchiveOutputDTO.ArchiveMemberDTO.builder()
                .username(archiveMember.getMember().getUsername())
                .nickname(archiveMember.getMember().getNickname())
                .role(archiveMember.getRole())
                .build())
            .collect(Collectors.toList());

        // 북마크 관련 정보 조회
        boolean isBookmarked = bookmarkRepository.existsByArchiveAndUsername(archive, username);
        long bookmarkCount = bookmarkRepository.countByArchive(archive);

        // 댓글 정보 조회
        List<ArchiveCommentOutputDTO> commentDTOs = new ArrayList<>();
        Page<ArchiveComment> parentComments = commentRepository.findParentCommentsByArchiveId(archive.getId(), Pageable.unpaged());
        
        for (ArchiveComment parentComment : parentComments) {
            ArchiveCommentOutputDTO.CommentDTO parentDTO = ArchiveCommentOutputDTO.CommentDTO.builder()
                    .id(parentComment.getId())
                    .writer(parentComment.getWriter())
                    .content(parentComment.isDeleted() ? "삭제된 댓글입니다." : parentComment.getContent())
                    .isDeleted(parentComment.isDeleted())
                    .createdDateTime(parentComment.getCreatedDateTime())
                    .modifiedDateTime(parentComment.getModifiedDateTime())
                    .build();

            ArchiveCommentOutputDTO commentOutputDTO = new ArchiveCommentOutputDTO(parentDTO);

            // 대댓글 조회
            List<ArchiveComment> childComments = commentRepository.findRepliesByParentId(parentComment.getId());
            for (ArchiveComment childComment : childComments) {
                ArchiveCommentOutputDTO.CommentDTO childDTO = ArchiveCommentOutputDTO.CommentDTO.builder()
                        .id(childComment.getId())
                        .writer(childComment.getWriter())
                        .content(childComment.isDeleted() ? "삭제된 댓글입니다." : childComment.getContent())
                        .isDeleted(childComment.isDeleted())
                        .createdDateTime(childComment.getCreatedDateTime())
                        .modifiedDateTime(childComment.getModifiedDateTime())
                        .build();
                commentOutputDTO.addChildComment(childDTO);
            }

            commentDTOs.add(commentOutputDTO);
        }

        return ArchiveOutputDTO.builder()
                .id(archive.getId())
                .title(archive.getTitle())
                .description(archive.getDescription())
                .startDate(archive.getStartDate())
                .endDate(archive.getEndDate())
                .duration(archive.getDurationAsString())
                .category(archive.getCategory())
                .thumbnail(archive.getThumbnail())
                .link(archive.getLink())
                .skills(archive.getSkillList())
                .imgUrls(archive.getImageUrlsAsList())
                .view(viewCount)
                .isBookmarked(isBookmarked)
                .bookmarkCount((int) bookmarkCount)
                .members(memberDTOs)
                .createdDateTime(archive.getCreatedDateTime())
                .modifiedDateTime(archive.getModifiedDateTime())
                .comments(commentDTOs)
                .build();
    }

    public ArchivePageResponseDTO createArchivePageResponse(Page<Archive> archivePage, String username) {
        return ArchivePageResponseDTO.builder()
                .archives(archivePage.getContent().stream()
                        .map(archive -> convertToDTO(archive, username, archive.getView()))
                        .toList())
                .totalPages(archivePage.getTotalPages())
                .build();
    }
} 