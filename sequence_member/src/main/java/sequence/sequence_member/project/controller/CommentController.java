package sequence.sequence_member.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.project.dto.CommentInputDTO;
import sequence.sequence_member.project.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponseData<String>> writeComment(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long projectId, @RequestBody
                                                                CommentInputDTO commentInputDTO){
        commentService.writeComment(customUserDetails, projectId , commentInputDTO);
        return ResponseEntity.ok().body(ApiResponseData.success(null, "댓글 작성 성공"));
    }
}
