package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sequence.sequence_member.archive.dto.*;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveComment;
import sequence.sequence_member.archive.repository.ArchiveCommentRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveCommentService {

    private final ArchiveCommentRepository commentRepository;
    private final ArchiveRepository archiveRepository;

    // 댓글 작성
    @Transactional
    public Long createComment(Long archiveId, CommentCreateRequestDTO dto) {
        // 아카이브 검증
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아카이브를 찾을 수 없습니다."));

        // 대댓글인 경우 부모 댓글 검증
        ArchiveComment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "원본 댓글을 찾을 수 없습니다."));
            
            // 대댓글의 대댓글 작성 방지
            if (parent.isReply()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대댓글에는 댓글을 작성할 수 없습니다.");
            }
        }

        ArchiveComment comment = ArchiveComment.builder()
                .archive(archive)
                .writer(dto.getWriter())
                .parent(parent)
                .content(dto.getContent())
                .isDeleted(false)
                .build();

        return commentRepository.save(comment).getId();
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long archiveId, Long commentId, String writer, CommentUpdateRequestDTO dto) {
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아카이브를 찾을 수 없습니다."));

        ArchiveComment comment = commentRepository.findByIdAndArchiveAndWriter(commentId, archive, writer)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (comment.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.updateContent(dto.getContent());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long archiveId, Long commentId, String writer) {
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아카이브를 찾을 수 없습니다."));

        ArchiveComment comment = commentRepository.findByIdAndArchiveAndWriter(commentId, archive, writer)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 이미 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 삭제된 댓글입니다.");
        }

        comment.delete();
    }

    // 댓글 목록 조회 (페이징)
    public CommentPageResponseDTO getComments(Long archiveId, int page) {
        // 아카이브 존재 확인
        if (!archiveRepository.existsById(archiveId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "아카이브를 찾을 수 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, 10);  // 한 페이지당 10개 댓글
        Page<ArchiveComment> commentPage = commentRepository.findParentCommentsByArchiveId(archiveId, pageable);

        List<CommentResponseDTO> commentDTOs = commentPage.getContent().stream()
            .map(comment -> {
                CommentResponseDTO dto = CommentResponseDTO.from(comment);
                // 대댓글이 있는 경우 대댓글 목록 추가
                List<CommentResponseDTO> replies = commentRepository.findRepliesByParentId(comment.getId())
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