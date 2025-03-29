package sequence.sequence_member.archive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.CommentCreateRequestDTO;
import sequence.sequence_member.archive.dto.CommentUpdateRequestDTO;
import sequence.sequence_member.archive.dto.CommentPageResponseDTO;
import sequence.sequence_member.archive.service.ArchiveCommentService;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class ArchiveCommentController {

    private final ArchiveCommentService commentService;

    // 댓글 작성
    @PostMapping("/{archiveId}/comments")
    public ResponseEntity<ApiResponseData<Long>> createComment(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentCreateRequestDTO requestDto) {
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean exists = commentService.checkArchiveExists(archiveId);
        if (!exists) {
            throw new CanNotFindResourceException("아카이브를 찾을 수 없습니다.");
        }
        
        Long commentId = commentService.createComment(archiveId, requestDto);

        return ResponseEntity
            .status(Code.CREATED.getStatus())
            .body(ApiResponseData.of(
                Code.CREATED.getCode(),
                "댓글이 등록되었습니다.",
                commentId
            ));
    }

    // 댓글 수정
    @PutMapping("/{archiveId}/comments/{commentId}")
    public ResponseEntity<ApiResponseData<Void>> updateComment(
            @PathVariable Long archiveId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentUpdateRequestDTO requestDto) {
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean success = commentService.updateComment(
            archiveId, 
            commentId, 
            userDetails.getUsername(),
            requestDto
        );

        if (!success) {
            throw new CanNotFindResourceException("아카이브 또는 댓글을 찾을 수 없거나 이미 삭제된 댓글입니다.");
        }

        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "댓글이 수정되었습니다.",
            null
        ));
    }

    // 댓글 삭제
    @DeleteMapping("/{archiveId}/comments/{commentId}")
    public ResponseEntity<ApiResponseData<Void>> deleteComment(
            @PathVariable Long archiveId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean success = commentService.deleteComment(
            archiveId, 
            commentId, 
            userDetails.getUsername()
        );
        
        if (!success) {
            throw new CanNotFindResourceException("아카이브 또는 댓글을 찾을 수 없거나 이미 삭제된 댓글입니다.");
        }

        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "댓글이 삭제되었습니다.",
            null
        ));
    }

    // 댓글 목록 조회
    @GetMapping("/{archiveId}/comments")
    public ResponseEntity<ApiResponseData<CommentPageResponseDTO>> getComments(
            @PathVariable Long archiveId,
            @RequestParam(defaultValue = "0") int page) {
        
        boolean exists = commentService.checkArchiveExists(archiveId);
        if (!exists) {
            throw new CanNotFindResourceException("아카이브를 찾을 수 없습니다.");
        }
        
        CommentPageResponseDTO response = commentService.getComments(archiveId, page);
        
        if (response.getComments().isEmpty()) {
            return ResponseEntity.ok(ApiResponseData.of(
                Code.CAN_NOT_FIND_RESOURCE.getCode(),
                "댓글이 없습니다.",
                response
            ));
        }

        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "댓글 목록을 조회했습니다.",
            response
        ));
    }
} 