package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.*;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveComment;
import sequence.sequence_member.archive.repository.ArchiveCommentRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveCommentService {

    private final ArchiveCommentRepository commentRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;

    // 아카이브 존재 여부 확인 (삭제되지 않은 것만)
    public boolean checkArchiveExists(Long archiveId) {
        return archiveRepository.existsByIdAndIsDeletedFalse(archiveId);
    }

    // 댓글 작성
    @Transactional
    public Long createComment(Long archiveId, String username, CommentCreateRequestDTO dto) {
        // username으로 사용자 조회하여 nickname 가져오기
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
        
        // 아카이브 조회 (삭제되지 않은 것만)
        Optional<Archive> archiveOpt = archiveRepository.findByIdAndIsDeletedFalse(archiveId);
        if (archiveOpt.isEmpty()) {
            return null;
        }
        
        Archive archive = archiveOpt.get();

        // 대댓글인 경우 부모 댓글 검증
        ArchiveComment parent = null;
        if (dto.getParentId() != null) {
            Optional<ArchiveComment> parentOpt = commentRepository.findByIdAndIsDeletedFalse(dto.getParentId());
            if (parentOpt.isEmpty()) {
                return null;
            }
            
            parent = parentOpt.get();
            
            // 대댓글의 대댓글 작성 방지
            if (parent.isReply()) {
                return null;
            }
        }

        ArchiveComment comment = ArchiveComment.builder()
                .archive(archive)
                .writer(member.getNickname())  // 조회한 사용자의 nickname을 writer로 저장
                .parent(parent)
                .content(dto.getContent())
                .build();

        return commentRepository.save(comment).getId();
    }

    // 댓글 수정
    @Transactional
    public boolean updateComment(Long archiveId, Long commentId, String username, CommentUpdateRequestDTO dto) {
        // username으로 사용자 정보 조회
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
        
        Optional<Archive> archiveOpt = archiveRepository.findByIdAndIsDeletedFalse(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();

        Optional<ArchiveComment> commentOpt = commentRepository.findByIdAndArchiveAndWriterAndIsDeletedFalse(
            commentId, 
            archive, 
            member.getNickname()  // 조회한 사용자의 nickname 사용
        );
        if (commentOpt.isEmpty()) {
            return false;
        }
        
        ArchiveComment comment = commentOpt.get();

        comment.updateContent(dto.getContent());
        return true;
    }

    // 댓글 삭제 (소프트 삭제)
    @Transactional
    public boolean deleteComment(Long archiveId, Long commentId, String username) {
        // username으로 사용자 조회하여 nickname 가져오기
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
        
        Optional<Archive> archiveOpt = archiveRepository.findByIdAndIsDeletedFalse(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();

        // nickname으로 댓글 작성자 검증 (삭제되지 않은 댓글만)
        Optional<ArchiveComment> commentOpt = commentRepository.findByIdAndArchiveAndWriterAndIsDeletedFalse(
            commentId, 
            archive, 
            member.getNickname()  // 조회한 사용자의 nickname으로 검증
        );
        if (commentOpt.isEmpty()) {
            return false;
        }
        
        ArchiveComment comment = commentOpt.get();

        // 소프트 삭제 적용
        comment.softDelete(username);
        commentRepository.save(comment);
        return true;
    }

    // 댓글 목록 조회 (페이징) - 삭제되지 않은 댓글만
    public CommentPageResponseDTO getComments(Long archiveId, int page) {
        // 아카이브 존재 확인 (삭제되지 않은 것만)
        if (!archiveRepository.existsByIdAndIsDeletedFalse(archiveId)) {
            return CommentPageResponseDTO.builder()
                    .comments(List.of())
                    .totalPages(0)
                    .totalElements(0L)
                    .build();
        }

        Pageable pageable = PageRequest.of(page, 10);  // 한 페이지당 10개 댓글
        Page<ArchiveComment> commentPage = commentRepository.findByArchiveIdAndParentIsNullAndIsDeletedFalseOrderByCreatedDateTimeAsc(archiveId, pageable);

        List<CommentResponseDTO> commentDTOs = commentPage.getContent().stream()
            .map(comment -> {
                CommentResponseDTO dto = CommentResponseDTO.from(comment);
                // 대댓글이 있는 경우 대댓글 목록 추가 (삭제되지 않은 것만)
                List<CommentResponseDTO> replies = commentRepository.findByParentIdAndIsDeletedFalseOrderByCreatedDateTimeAsc(comment.getId())
                    .stream()
                    .map(CommentResponseDTO::from)
                    .collect(Collectors.toList());
                return dto.toBuilder()
                    .replies(replies)
                    .build();
            })
            .collect(Collectors.toList());

        return CommentPageResponseDTO.builder()
                .comments(commentDTOs)
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .build();
    }
} 