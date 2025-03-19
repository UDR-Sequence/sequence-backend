package sequence.sequence_member.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.service.ArchiveBookmarkService;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;

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
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean exists = bookmarkService.checkArchiveExists(archiveId);
        if (!exists) {
            throw new CanNotFindResourceException("아카이브를 찾을 수 없습니다.");
        }
        
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
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        List<Long> bookmarkedArchives = bookmarkService.getUserBookmarks(userDetails.getUsername());
        
        if (bookmarkedArchives.isEmpty()) {
            return ResponseEntity.ok(ApiResponseData.of(
                Code.CAN_NOT_FIND_RESOURCE.getCode(),
                "북마크한 아카이브가 없습니다.",
                bookmarkedArchives
            ));
        }
        
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
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean exists = bookmarkService.checkArchiveExists(archiveId);
        if (!exists) {
            throw new CanNotFindResourceException("아카이브를 찾을 수 없습니다.");
        }
        
        boolean isBookmarked = bookmarkService.isBookmarked(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "북마크 여부를 확인했습니다.",
            isBookmarked
        ));
    }
} 