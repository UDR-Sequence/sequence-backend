package sequence.sequence_member.archive.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPageResponseDTO {
    private List<CommentResponseDTO> comments;
    private int totalPages;
    private long totalElements;
} 