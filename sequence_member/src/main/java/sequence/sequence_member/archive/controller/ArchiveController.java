package sequence.sequence_member.archive.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.archive.dto.ArchiveOutputDTO;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.dto.ArchiveRegisterInputDTO;
import sequence.sequence_member.archive.dto.ArchiveUpdateDTO;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;  

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class ArchiveController {

    private final ArchiveService archiveService;
    
    @Value("${minio.archive_img}")
    private String ARCHIVE_IMG_BUCKET;
    
    @Value("${minio.archive_thumbnail}")
    private String ARCHIVE_THUMBNAIL_BUCKET;

    // 아카이브 등록 - form-data로 변경
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseData<Long>> createArchive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("archiveData") @Valid ArchiveRegisterInputDTO archiveRegisterInputDTO,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws Exception {
            
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        Long archiveId = archiveService.createArchiveWithImages(
            archiveRegisterInputDTO, 
            userDetails.getUsername(),
            thumbnailFile,
            imageFiles
        );
        
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            "아카이브 등록 성공",
            archiveId
        ));
    }

    // 아카이브 등록 후 결과 조회
    @GetMapping("/{archiveId}")
    public ResponseEntity<ApiResponseData<ArchiveOutputDTO>> getArchiveById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("archiveId") Long archiveId,
            HttpServletRequest request) {
        
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        
        ArchiveOutputDTO archiveDTO = archiveService.getArchiveById(archiveId, username, request);
        
        if (archiveDTO == null) {
            throw new CanNotFindResourceException("아카이브가 없습니다.");
        }
        
        return ResponseEntity.ok().body(ApiResponseData.of(
            Code.SUCCESS.getCode(), 
            "아카이브 상세 조회 성공", 
            archiveDTO
        ));
    }

    // 아카이브 수정
    @PutMapping(value = "/{archiveId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseData<String>> updateArchive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("archiveId") Long archiveId,
            @RequestPart("archiveData") @Valid ArchiveUpdateDTO archiveUpdateDTO,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws Exception {
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean isUpdated = archiveService.updateArchiveWithImages(
            archiveId, 
            archiveUpdateDTO, 
            userDetails.getUsername(),
            thumbnailFile,
            imageFiles
        );
        
        if (!isUpdated) {
            throw new CanNotFindResourceException("수정할 아카이브가 없거나 작성자만 수정할 수 있습니다.");
        }
        
        return ResponseEntity.ok(ApiResponseData.success(null, "아카이브 수정 성공"));
    }

    // 아카이브 삭제
    @DeleteMapping("/{archiveId}")
    public ResponseEntity<ApiResponseData<String>> deleteArchiveById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("archiveId") Long archiveId) {
        
        if (userDetails == null) {
            throw new BAD_REQUEST_EXCEPTION("로그인이 필요합니다.");
        }
        
        boolean isDeleted = archiveService.deleteArchive(archiveId, userDetails.getUsername());
        
        if (!isDeleted) {
            throw new CanNotFindResourceException("삭제할 아카이브가 없거나 작성자만 삭제할 수 있습니다.");
        }
        
        return ResponseEntity.ok(ApiResponseData.success(null, "아카이브 삭제 성공"));
    }

    // 전체 리스트 조회
    @GetMapping("/projects")
    public ResponseEntity<ApiResponseData<ArchivePageResponseDTO>> getArchiveList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        
        ArchivePageResponseDTO response = archiveService.getAllArchives(page, sortType, username);
        
        if (response.getArchives().isEmpty()) {
            throw new CanNotFindResourceException("아카이브가 없습니다.");
        }
        
        return ResponseEntity.ok().body(ApiResponseData.of(
                Code.SUCCESS.getCode(),
                "아카이브 프로젝트 리스트 조회에 성공했습니다.",
                response
        ));
    }

    // 검색
    @GetMapping("/projects/search")
    public ResponseEntity<ApiResponseData<ArchivePageResponseDTO>> searchArchives(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        
        ArchivePageResponseDTO response = archiveService.searchArchives(category, keyword, page, sortType, username);
        
        if (response.getArchives().isEmpty()) {
            throw new CanNotFindResourceException("아카이브가 없습니다.");
        }
        
        return ResponseEntity.ok().body(ApiResponseData.of(
                Code.SUCCESS.getCode(),
                "검색 결과를 성공적으로 조회했습니다.",
                response
        ));
    }
} 