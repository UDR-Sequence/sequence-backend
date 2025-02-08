package sequence.sequence_member.archive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.ArchiveOutputDTO;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.dto.ArchiveRegisterInputDTO;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class ArchiveController {

    private final ArchiveService archiveService;

    @PostMapping
    public ResponseEntity<ArchiveOutputDTO> createArchive(@Valid @RequestBody ArchiveRegisterInputDTO archiveRegisterInputDTO) {
        ArchiveOutputDTO response = archiveService.createArchive(archiveRegisterInputDTO);
        return ResponseEntity.ok(response);
    }

    // 전체 리스트 조회
    @GetMapping("/projects")
    public ResponseEntity<ApiResponseData<ArchivePageResponseDTO>> getArchiveList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        
        return ResponseEntity.ok().body(ApiResponseData.of(
                Code.SUCCESS.getCode(),
                "아카이브 프로젝트 리스트 조회에 성공했습니다.",
                archiveService.getAllArchives(page, sortType)
        ));
    }

    // 검색 (카테고리 또는 키워드)
    @GetMapping("/projects/search")
    public ResponseEntity<ApiResponseData<ArchivePageResponseDTO>> searchArchives(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        
        ArchivePageResponseDTO response;
        
        if (category != null) {
            response = archiveService.searchByCategory(category, page, sortType);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            response = archiveService.searchByTitle(keyword, page, sortType);
        } else {
            response = archiveService.getAllArchives(page, sortType);
        }
        
        return ResponseEntity.ok().body(ApiResponseData.of(
                Code.SUCCESS.getCode(),
                "검색 결과를 성공적으로 조회했습니다.",
                response
        ));
    }
} 