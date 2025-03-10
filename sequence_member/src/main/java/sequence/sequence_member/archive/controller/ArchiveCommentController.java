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
        
        Long commentId = commentService.createComment(
            archiveId, 
            requestDto
        );

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
            @Valid @RequestBody CommentUpdateRequestDTO requestDto) {
        
        commentService.updateComment(
            archiveId, 
            commentId, 
            requestDto.getWriter(),
            requestDto
        );

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
            @RequestBody CommentUpdateRequestDTO requestDto) {
        
        commentService.deleteComment(
            archiveId, 
            commentId, 
            requestDto.getWriter()
        );

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
        
        CommentPageResponseDTO response = commentService.getComments(archiveId, page);

        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "댓글 목록을 조회했습니다.",
            response
        ));
    }
} 