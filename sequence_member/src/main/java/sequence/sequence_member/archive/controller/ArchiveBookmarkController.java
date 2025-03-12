package sequence.sequence_member.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.service.ArchiveBookmarkService;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import sequence.sequence_member.member.dto.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class ArchiveBookmarkController {
    
    private final ArchiveBookmarkService bookmarkService;

    // 북마크 토글 (추가/삭제)
    @PostMapping("/{archiveId}/bookmark")
    public ResponseEntity<ApiResponseData<Boolean>> toggleBookmark(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        boolean isBookmarked = bookmarkService.toggleBookmark(archiveId, userDetails.getUsername());
        
        String message = isBookmarked ? "북마크가 추가되었습니다." : "북마크가 취소되었습니다.";
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            message,
            isBookmarked
        ));
    }

    // 사용자의 북마크 목록 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<ApiResponseData<List<Long>>> getUserBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        List<Long> bookmarkedArchives = bookmarkService.getUserBookmarks(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "북마크 목록을 조회했습니다.",
            bookmarkedArchives
        ));
    }

    // 특정 아카이브의 북마크 여부 확인
    @GetMapping("/{archiveId}/bookmark/check")
    public ResponseEntity<ApiResponseData<Boolean>> isBookmarked(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        boolean isBookmarked = bookmarkService.isBookmarked(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "북마크 여부를 확인했습니다.",
            isBookmarked
        ));
    }
} 