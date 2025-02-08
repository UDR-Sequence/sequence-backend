package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivePageResponseDTO {
    private List<ArchiveOutputDTO> archives;  // 현재 페이지의 아카이브 목록
    private int totalPages;                   // 전체 페이지 수
} 