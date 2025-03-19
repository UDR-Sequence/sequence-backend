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
import sequence.sequence_member.archive.dto.UserArchiveDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.member.dto.CustomUserDetails;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import sequence.sequence_member.global.minio.service.MinioService;
import java.util.Optional;
import sequence.sequence_member.archive.repository.TeamEvaluationRepository;
import org.springframework.web.server.ResponseStatusException;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;

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
    private final MinioService minioService;
    private final TeamEvaluationRepository teamEvaluationRepository;
    
    @Value("${MINIO_ARCHIVE_IMG}")
    private String ARCHIVE_IMG_BUCKET;

    public ArchiveOutputDTO getArchiveById(Long archiveId, String username, HttpServletRequest request) {
        // Optional로 아카이브 조회
        Optional<Archive> archiveOptional = archiveRepository.findById(archiveId);
        
        // 아카이브가 없으면 null 반환
        if (archiveOptional.isEmpty()) {
            return null;
        }
        
        Archive archive = archiveOptional.get();
        
        // Redis에서 조회수 처리
        int viewCount = archiveViewService.getViewsFromRedis(request, archiveId);
        
        return convertToDTO(archive, username, viewCount);
    }

    @Transactional
    public boolean updateArchive(Long archiveId, ArchiveUpdateDTO archiveUpdateDTO, String username) {
        // 사용자 검증
        Optional<MemberEntity> memberOpt = memberRepository.findByUsername(username);
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        MemberEntity member = memberOpt.get();

        // 아카이브 멤버 검증
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            return false;
        }

        // 아카이브 존재 여부 확인
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();
        
        // 아카이브 기본 정보 업데이트
        archive.updateArchive(archiveUpdateDTO);
        
        // 기존 팀원 정보 삭제
        archiveMemberRepository.deleteByArchiveId(archiveId);
        
        // 새로운 팀원 정보 등록
        
        return true;
    }

    @Transactional
    public boolean deleteArchive(Long archiveId, String username) {
        // 사용자 검증
        Optional<MemberEntity> memberOpt = memberRepository.findByUsername(username);
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        MemberEntity member = memberOpt.get();
        
        // 아카이브 존재 여부 확인
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();
        
        // 아카이브 멤버 검증
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            return false;
        }
        
        // 관련 엔티티를 순서대로 삭제 (참조 무결성 유지)
        // 1. 팀 평가 삭제
        teamEvaluationRepository.deleteByArchiveId(archiveId);
        
        // 2. 댓글 삭제
        commentRepository.deleteByArchiveId(archiveId);
        
        // 3. 북마크 삭제
        bookmarkRepository.deleteByArchiveId(archiveId);
        
        // 4. 아카이브 멤버 삭제
        archiveMemberRepository.deleteByArchiveId(archiveId);
        
        // 5. 최종적으로 아카이브 삭제
        archiveRepository.delete(archive);
        
        return true;
    }

    @Transactional(readOnly = true)
    public List<UserArchiveDTO> getUserArchiveList(CustomUserDetails customUserDetails){
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        List<Archive> latestArchives = archiveRepository.findTop5ByArchiveMembers_Member_IdOrderByCreatedDateTimeDesc((member.getId()));
        List<UserArchiveDTO> userArchiveDTOList = new ArrayList<>();
        for(Archive archive : latestArchives){
            userArchiveDTOList.add(new UserArchiveDTO(archive));
        }
        return userArchiveDTOList;
    }

    public ArchivePageResponseDTO getAllArchives(int page, SortType sortType, String username) {
        // username이 null이 아닐 때만 사용자 검증
        if (username != null) {
            memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));
        }

        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage = archiveRepository.findAll(pageable);
        
        // 비어있는 경우에도 빈 DTO 반환
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

        // 비어있는 경우에도 빈 DTO 반환
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
                .profileImg(archiveMember.getProfileImg())  // 프로필 이미지 추가
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
                .writerNickname(archive.getWriter().getNickname())
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
        List<ArchiveOutputDTO> archives = archivePage.isEmpty() ? 
                new ArrayList<>() : 
                archivePage.getContent().stream()
                    .map(archive -> convertToDTO(archive, username, archive.getView()))
                    .toList();
        
        return ArchivePageResponseDTO.builder()
                .archives(archives)
                .totalPages(archivePage.getTotalPages())
                .build();
    }

    @Transactional
    public Long createArchiveWithImages(ArchiveRegisterInputDTO dto, String username, List<MultipartFile> imageFiles) {
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));

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
        
        // 이미지 업로드 처리 - 이미지가 있을 때만 진행
        List<String> imageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                // 빈 파일이 아닐 때만 처리
                if (!imageFile.isEmpty()) {
                    try {
                        String fileName = "archive_" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                        String imageUrl = minioService.uploadFileMinio(ARCHIVE_IMG_BUCKET, fileName, imageFile);
                        imageUrls.add(imageUrl);
                    } catch (Exception e) {
                        // 전역 예외 처리기로 예외를 전달
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 업로드 실패: " + e.getMessage());
                    }
                }
            }
            // 이미지 URL이 있을 때만 설정
            if (!imageUrls.isEmpty()) {
                archive.setImageUrlsFromList(imageUrls);
            }
        }
        // 이미지가 없을 경우 빈 문자열 설정 (NULL 방지)
        else {
            archive.setImageUrlsFromList(new ArrayList<>());
        }
        
        Archive savedArchive = archiveRepository.save(archive);


        return savedArchive.getId();
    }

    @Transactional
    public boolean updateArchiveWithImages(Long archiveId, ArchiveUpdateDTO archiveUpdateDTO, 
                                        String username, List<MultipartFile> newImageFiles) {
        // 사용자 검증
        Optional<MemberEntity> memberOpt = memberRepository.findByUsername(username);
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        MemberEntity member = memberOpt.get();

        // 아카이브 멤버 검증
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            return false;
        }

        // 아카이브 존재 여부 확인
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();
        
        // 아카이브 기본 정보 업데이트
        archive.updateArchive(archiveUpdateDTO);
        
        // 새로운 이미지 처리 - 기존 이미지에 추가
        List<String> newImageUrls = new ArrayList<>();
        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            for (MultipartFile imageFile : newImageFiles) {
                if (!imageFile.isEmpty()) {
                    try {
                        String fileName = "archive_" + archive.getId() + "_" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                        String imageUrl = minioService.uploadFileMinio(ARCHIVE_IMG_BUCKET, fileName, imageFile);
                        newImageUrls.add(imageUrl);
                    } catch (Exception e) {
                        throw new BAD_REQUEST_EXCEPTION("이미지 업로드 실패: " + e.getMessage());
                    }
                }
            }
            
            // 이미지 URL 설정 - DTO에서 받은 이미지 URL과 새로 업로드한 이미지 URL 합치기
            List<String> allImageUrls = new ArrayList<>();
            if (archiveUpdateDTO.getImgUrls() != null) {
                allImageUrls.addAll(archiveUpdateDTO.getImgUrls());
            }
            allImageUrls.addAll(newImageUrls);
            
            archive.setImageUrlsFromList(allImageUrls);
        } else if (archiveUpdateDTO.getImgUrls() != null) {
            archive.setImageUrlsFromList(archiveUpdateDTO.getImgUrls());
        } else {
            archive.setImageUrlsFromList(new ArrayList<>());
        }
        
        return true;
    }
} 