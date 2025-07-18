package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.ArchiveOutputDTO;
import sequence.sequence_member.archive.dto.ArchiveRegisterInputDTO;
import sequence.sequence_member.archive.dto.ArchiveUpdateDTO;
import sequence.sequence_member.archive.dto.ArchiveMemberDTO;
import sequence.sequence_member.archive.dto.UserArchiveDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.global.exception.UserNotFindException;
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

import java.util.Optional;
import sequence.sequence_member.archive.repository.TeamEvaluationRepository;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.exception.AuthException;
import sequence.sequence_member.archive.dto.ArchiveListDTO;

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
    private final ArchiveFileService archiveFileService;
    private final TeamEvaluationService teamEvaluationService;
    
    @Value("${MINIO_ARCHIVE_IMG}")
    private String ARCHIVE_IMG_BUCKET;
    
    @Value("${MINIO_ARCHIVE_THUMBNAIL}")
    private String ARCHIVE_THUMBNAIL_BUCKET;  // 썸네일 버킷 추가

    public ArchiveOutputDTO getArchiveById(Long archiveId, String username, HttpServletRequest request) {
        // Optional로 아카이브 조회 (삭제되지 않은 것만)
        Optional<Archive> archiveOptional = archiveRepository.findByIdAndIsDeletedFalse(archiveId);
        
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
    public boolean updateArchive(Long archiveId, ArchiveUpdateDTO dto, String username,
            MultipartFile thumbnailFile, List<MultipartFile> newImageFiles) throws Exception {
            
        Archive archive = validateAndGetArchive(archiveId, username);
        
        // 기본 정보 업데이트
        updateBasicInfo(archive, dto);
        
        // 썸네일 업데이트
        String thumbnailUrl = archiveFileService.uploadThumbnail(archiveId, thumbnailFile);
        archive.setThumbnail(thumbnailUrl != null ? thumbnailUrl : dto.getThumbnail());
        
        // 이미지 업데이트
        List<String> finalImageUrls = new ArrayList<>();
        if (dto.getImgUrls() != null) {
            finalImageUrls.addAll(dto.getImgUrls());
        }
        finalImageUrls.addAll(archiveFileService.uploadImages(archiveId, newImageFiles));
        archive.setImageUrlsFromList(finalImageUrls);
        
        return true;
    }

    private void updateBasicInfo(Archive archive, ArchiveUpdateDTO dto) {
        archive.setTitle(dto.getTitle());
        archive.setDescription(dto.getDescription());
        archive.setStartDate(dto.getStartDate());
        archive.setEndDate(dto.getEndDate());
        archive.setCategory(dto.getCategory());
        archive.setLink(dto.getLink());
        archive.setSkillsFromList(dto.getSkills());
    }

    @Transactional
    public boolean deleteArchive(Long archiveId, String username) {
        // 사용자 검증
        Optional<MemberEntity> memberOpt = memberRepository.findByUsernameAndIsDeletedFalse(username);
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        MemberEntity member = memberOpt.get();
        
        // 아카이브 존재 여부 확인 (삭제되지 않은 것만)
        Optional<Archive> archiveOpt = archiveRepository.findByIdAndIsDeletedFalse(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();
        
        // 작성자 검증 - 작성자만 삭제 가능
        if (!archive.getWriter().getId().equals(member.getId())) {
            return false;
        }
        
        // 소프트 삭제 적용
        archive.softDelete(username);
        archiveRepository.save(archive);
        
        return true;
    }

    @Transactional(readOnly = true)
    public List<UserArchiveDTO> getUserArchiveListAtAlarm(CustomUserDetails customUserDetails){
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("사용자를 찾을 수 없습니다."));

        List<Archive> latestArchives = archiveRepository.findTop10ByMemberId(member.getId());
        List<UserArchiveDTO> userArchiveDTOList = new ArrayList<>();

        // 평가전 상태인 아카이브만 리스트에 추가
        for(Archive archive : latestArchives){
            List<ArchiveMember> archiveMembers = archiveMemberRepository.findAllByArchiveId(archive.getId());
            ArchiveMember archiveMember = archiveMembers.stream()
                .filter(self -> self.getMember().getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new UserNotFindException( "사용자를 찾을 수 없습니다."));
            Status memberStatus = teamEvaluationService.getArvhiceMemberEvaluationStatus(archiveMember, archiveMembers);
            if(memberStatus == Status.평가전){
                userArchiveDTOList.add(new UserArchiveDTO(archive));
            }
        }
        return userArchiveDTOList;
    }

    @Transactional(readOnly = true)
    public List<UserArchiveDTO> getUserArchiveList(CustomUserDetails customUserDetails) {
        // 사용자 검증
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

        // 평가완료 상태인 아카이브만 조회 (삭제되지 않은 것만)
        List<Archive> latestArchives = archiveRepository.findTop5ByMemberIdAndStatus(member.getId(), Status.평가완료);
        
        List<UserArchiveDTO> userArchiveDTOList = new ArrayList<>();
        for(Archive archive : latestArchives){
            userArchiveDTOList.add(new UserArchiveDTO(archive));
        }
        return userArchiveDTOList;
    }

    public ArchiveListDTO getAllArchives(int page, SortType sortType, String username) {
        if (username != null) {
            memberRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
        }

        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage = archiveRepository.findByStatusAndIsDeletedFalseWithWriter(Status.평가완료, pageable);
        
        List<ArchiveListDTO.ArchiveSimpleDTO> archives = archivePage.getContent().stream()
            .map(archive -> {
                // 작성자를 제외한 멤버 수 계산
                int otherMemberCount = archive.getArchiveMembers().size() - 1; // 작성자 제외
                if (otherMemberCount < 0) otherMemberCount = 0; // 음수 방지
                
                return ArchiveListDTO.ArchiveSimpleDTO.builder()
                    .id(archive.getId())
                    .title(archive.getTitle())
                    .writerNickname(archive.getWriter().getNickname())
                    .writerProfileImg(archive.getWriter().getProfileImg())
                    .thumbnail(archive.getThumbnail())
                    .commentCount(archive.getComments().size())
                    .view(archive.getView())
                    .bookmarkCount((int) bookmarkRepository.countByArchive(archive))
                    .otherMemberCount(otherMemberCount)
                    .createdDateTime(archive.getCreatedDateTime())
                    .build();
            })
            .toList();

        return ArchiveListDTO.builder()
            .archives(archives)
            .totalPages(archivePage.getTotalPages())
            .totalElements(archivePage.getTotalElements())
            .build();
    }

    public ArchiveListDTO searchArchives(
            Category category, 
            String keyword, 
            int page, 
            SortType sortType, 
            String username) {
        
        if (username != null) {
            memberRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
        }

        Pageable pageable = createPageableWithSort(page, sortType);
        Page<Archive> archivePage;
        
        if (category != null && keyword != null && !keyword.trim().isEmpty()) {
            archivePage = archiveRepository.findByCategoryAndTitleContainingIgnoreCaseAndStatusAndIsDeletedFalseWithWriter(
                category, keyword.trim(), Status.평가완료, pageable);
        } else if (category != null) {
            archivePage = archiveRepository.findByCategoryAndStatusAndIsDeletedFalseWithWriter(category, Status.평가완료, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            archivePage = archiveRepository.findByTitleContainingIgnoreCaseAndStatusAndIsDeletedFalseWithWriter(
                keyword.trim(), Status.평가완료, pageable);
        } else {
            archivePage = archiveRepository.findByStatusAndIsDeletedFalseWithWriter(Status.평가완료, pageable);
        }

        List<ArchiveListDTO.ArchiveSimpleDTO> archives = archivePage.getContent().stream()
            .map(archive -> {
                // 작성자를 제외한 멤버 수 계산
                int otherMemberCount = archive.getArchiveMembers().size() - 1; // 작성자 제외
                if (otherMemberCount < 0) otherMemberCount = 0; // 음수 방지
                
                return ArchiveListDTO.ArchiveSimpleDTO.builder()
                    .id(archive.getId())
                    .title(archive.getTitle())
                    .writerNickname(archive.getWriter().getNickname())
                    .writerProfileImg(archive.getWriter().getProfileImg())
                    .thumbnail(archive.getThumbnail())
                    .commentCount(archive.getComments().size())
                    .view(archive.getView())
                    .bookmarkCount((int) bookmarkRepository.countByArchive(archive))
                    .otherMemberCount(otherMemberCount)
                    .createdDateTime(archive.getCreatedDateTime())
                    .build();
            })
            .toList();

        return ArchiveListDTO.builder()
            .archives(archives)
            .totalPages(archivePage.getTotalPages())
            .totalElements(archivePage.getTotalElements())
            .build();
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

    // 댓글 작성자의 프로필 이미지를 가져오는 헬퍼 메서드
    private String getCommentWriterProfileImg(String writerNickname) {
        return memberRepository.findByNickname(writerNickname)
            .map(MemberEntity::getProfileImg)
            .orElse("default.png");  // 기본 이미지
    }

    // Archive 엔티티를 DTO로 변환
    private ArchiveOutputDTO convertToDTO(Archive archive, String username, int viewCount) {
        List<ArchiveOutputDTO.ArchiveMemberDTO> memberDTOs = archive.getArchiveMembers().stream()
            .filter(archiveMember -> !archiveMember.getMember().getId().equals(archive.getWriter().getId()))
            .map(archiveMember -> ArchiveOutputDTO.ArchiveMemberDTO.builder()
                .username(archiveMember.getMember().getUsername())
                .nickname(archiveMember.getMember().getNickname())
                .profileImg(archiveMember.getProfileImg())
                .build())
            .collect(Collectors.toList());

        // 북마크 관련 정보 조회 부분 수정
        boolean isBookmarked = false;
        if (username != null && !username.isEmpty()) {
            // username으로 MemberEntity 조회
            Optional<MemberEntity> memberOpt = memberRepository.findByUsernameAndIsDeletedFalse(username);
            if (memberOpt.isPresent()) {
                MemberEntity userId = memberOpt.get();
                isBookmarked = bookmarkRepository.existsByArchiveAndUserId(archive, userId);
            }
        }
        long bookmarkCount = bookmarkRepository.countByArchive(archive);

        // 댓글 정보 조회
        List<ArchiveCommentOutputDTO> commentDTOs = new ArrayList<>();
        Page<ArchiveComment> parentComments = commentRepository.findByArchiveIdAndParentIsNullOrderByCreatedDateTimeAsc(
            archive.getId(), 
            PageRequest.of(0, Integer.MAX_VALUE)  // 정렬 조건 제거
        );
        
        for (ArchiveComment parentComment : parentComments) {
            ArchiveCommentOutputDTO.CommentDTO parentDTO = ArchiveCommentOutputDTO.CommentDTO.builder()
                    .id(parentComment.getId())
                    .commentWriter(parentComment.getWriter())
                    .commentWriterProfileImg(getCommentWriterProfileImg(parentComment.getWriter()))
                    .content(parentComment.isDeleted() ? "삭제된 댓글입니다." : parentComment.getContent())
                    .isDeleted(parentComment.isDeleted())
                    .createdDateTime(parentComment.getCreatedDateTime())
                    .modifiedDateTime(parentComment.getModifiedDateTime())
                    .build();

            ArchiveCommentOutputDTO commentOutputDTO = new ArchiveCommentOutputDTO(parentDTO);

            // 대댓글 조회
            List<ArchiveComment> childComments = commentRepository.findByParentIdOrderByCreatedDateTimeAsc(parentComment.getId());
            for (ArchiveComment childComment : childComments) {
                ArchiveCommentOutputDTO.CommentDTO childDTO = ArchiveCommentOutputDTO.CommentDTO.builder()
                        .id(childComment.getId())
                        .commentWriter(childComment.getWriter())
                        .commentWriterProfileImg(getCommentWriterProfileImg(childComment.getWriter()))
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
                .writerUsername(archive.getWriter().getUsername())
                .writerNickname(archive.getWriter().getNickname())
                .writerProfileImg(archive.getWriter().getProfileImg())
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

    @Transactional
    public Long createArchiveWithImages(
            ArchiveRegisterInputDTO dto, 
            String username, 
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles) throws Exception {
        
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));

        Archive archive = createArchiveEntity(dto, member);
        
        // 썸네일 업로드 및 설정
        String thumbnailUrl = archiveFileService.uploadThumbnail(archive.getId(), thumbnailFile);
        archive.setThumbnail(thumbnailUrl);
        
        // 이미지 업로드 및 설정
        List<String> imageUrls = archiveFileService.uploadImages(archive.getId(), imageFiles);
        archive.setImageUrlsFromList(imageUrls);
        
        Archive savedArchive = archiveRepository.save(archive);
        registerArchiveMembers(savedArchive, dto.getArchiveMembers(), member);
        
        return savedArchive.getId();
    }

    private Archive createArchiveEntity(ArchiveRegisterInputDTO dto, MemberEntity writer) {
        Archive archive = Archive.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .category(dto.getCategory())
                .status(Status.평가전)
                .link(dto.getLink())
                .writer(writer)
                .build();
        
        archive.setSkillsFromList(dto.getSkills());
        return archive;
    }

    private void registerArchiveMembers(Archive archive, List<ArchiveMemberDTO> memberDtos, MemberEntity writer) {
        boolean writerAdded = false;
        
        for (ArchiveMemberDTO memberDto : memberDtos) {
            MemberEntity member = memberRepository.findByNickname(memberDto.getNickname())
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("존재하지 않는 사용자입니다: " + memberDto.getNickname()));

            if (member.getId().equals(writer.getId())) {
                writerAdded = true;
            }

            saveArchiveMember(archive, member, memberDto.getProfileImg());
        }
        
        if (!writerAdded) {
            saveArchiveMember(archive, writer, writer.getProfileImg());
        }
    }

    private void saveArchiveMember(Archive archive, MemberEntity member, String profileImg) {
        ArchiveMember archiveMember = ArchiveMember.builder()
            .archive(archive)
            .member(member)
            .profileImg(profileImg != null ? profileImg : member.getProfileImg())
            .build();
        
        archiveMemberRepository.save(archiveMember);
    }

    private Archive validateAndGetArchive(Long archiveId, String username) {
        // 아카이브 조회 (삭제되지 않은 것만)
        Archive archive = archiveRepository.findByIdAndIsDeletedFalse(archiveId)
                .orElseThrow(() -> new CanNotFindResourceException("아카이브를 찾을 수 없습니다."));
        
        // 작성자 검증
        MemberEntity member = archive.getWriter();
        if (!member.getUsername().equals(username)) {
            throw new AuthException("아카이브 수정 권한이 없습니다.");
        }
        
        return archive;
    }

    @Transactional
    public boolean updateArchiveWithImages(
            Long archiveId, 
            ArchiveUpdateDTO dto, 
            String username,
            MultipartFile thumbnailFile,
            List<MultipartFile> newImageFiles) throws Exception {
        
        Archive archive = validateAndGetArchive(archiveId, username);
        
        // 기본 정보 업데이트
        updateBasicInfo(archive, dto);
        
        // 썸네일 업데이트
        String thumbnailUrl = archiveFileService.uploadThumbnail(archiveId, thumbnailFile);
        archive.setThumbnail(thumbnailUrl != null ? thumbnailUrl : dto.getThumbnail());
        
        // 이미지 업데이트
        List<String> finalImageUrls = new ArrayList<>();
        if (dto.getImgUrls() != null) {
            finalImageUrls.addAll(dto.getImgUrls());
        }
        finalImageUrls.addAll(archiveFileService.uploadImages(archiveId, newImageFiles));
        archive.setImageUrlsFromList(finalImageUrls);
        
        return true;
    }
} 