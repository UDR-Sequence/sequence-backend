package sequence.sequence_member.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.CommentInputDTO;
import sequence.sequence_member.project.entity.Comment;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.CommentRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    /**
     * 댓글을 작성하는 메인 로직 함수
     * @param customUserDetails
     * @param projectId
     * @param commentInputDTO
     */
    public void writeComment(CustomUserDetails customUserDetails, Long projectId, CommentInputDTO commentInputDTO){
        MemberEntity writer = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new UserNotFindException("해당 프로젝트가 존재하지 않습니다."));
        commentRepository.save(createComment(commentInputDTO, writer, project));
    }

    // 빌더패턴을 활용해 댓글을 생성하는 함수
    private Comment createComment(CommentInputDTO commentInputDTO, MemberEntity writer, Project project){
        Comment comment = Comment.builder()
                .project(project)
                .content(commentInputDTO.getContent())
                .writer(writer)
                .build();

        // 부모 댓글이 존재할 경우 부모 댓글을 설정
        if(commentInputDTO.getParentCommentId() != null){
            Comment parentComment = commentRepository.findById(commentInputDTO.getParentCommentId()).orElseThrow(()-> new CanNotFindResourceException("해당 부모 댓글이 존재하지 않습니다."));
            comment.setParentComment(parentComment);
        }

        return comment;
    }
}
