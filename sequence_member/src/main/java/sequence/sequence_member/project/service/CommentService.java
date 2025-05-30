package sequence.sequence_member.project.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.AuthException;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.CommentDTO;
import sequence.sequence_member.project.dto.CommentInputDTO;
import sequence.sequence_member.project.dto.CommentOutputDTO;
import sequence.sequence_member.project.dto.CommentUpdateDTO;
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
    @Transactional
    public void writeComment(CustomUserDetails customUserDetails, Long projectId, CommentInputDTO commentInputDTO){
        MemberEntity writer = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
        commentRepository.save(createComment(commentInputDTO, writer, project));
    }

    /**
     * 댓글을 수정하는 메인 로직 함수
     * @param customUserDetails
     * @param projectId
     * @param commentId
     * @param commentUpdateDTO
     */
    @Transactional
    public void updateComment(CustomUserDetails customUserDetails, Long projectId, Long commentId, CommentUpdateDTO commentUpdateDTO){

        //각 리소스가 존재하는지 확인
        MemberEntity writer = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 댓글이 존재하지 않습니다."));

        //작성자와 수정하려는 댓글의 작성자가 같은지 확인
        if(!comment.getWriter().equals(writer)){
            throw new AuthException("해당 댓글을 수정할 권한이 없습니다.");
        }

        //수정하려는 댓글의 프로젝트와 요청한 프로젝트가 같은지 확인
         if(!comment.getProject().equals(project)) {
             throw new BAD_REQUEST_EXCEPTION("해당 프로젝트에 존재하지 않는 댓글입니다.");
         }

         //수정하려는 댓글의 내용을 변경
        comment.setContent(commentUpdateDTO.getContent());
        commentRepository.save(comment);
    }

    /**
     * 댓글을 삭제하는 메인 로직 함수
     * @param customUserDetails
     * @param projectId
     * @param commentId
     */
    @Transactional
    public void deleteComment(CustomUserDetails customUserDetails, Long projectId, Long commentId){
        //각 리소스가 존재하는지 확인
        MemberEntity writer = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 댓글이 존재하지 않습니다."));

        //작성자와 수정하려는 댓글의 작성자가 같은지 확인
        if(!comment.getWriter().equals(writer)){
            throw new AuthException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        //삭제하려는 댓글의 프로젝트와 요청한 프로젝트가 같은지 확인
        if(!comment.getProject().equals(project)) {
            throw new CanNotFindResourceException("해당 프로젝트에 존재하지 않는 댓글입니다.");
        }

        commentRepository.delete(comment);
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

    // 프로젝트에 대한 댓글들을 조회하여 CommentOutputDTO로 변환하는 함수
    @NotNull
    public List<CommentOutputDTO> getCommentOutputDTOS(Project project) {
        // 댓글들을 조회하여 응답데이터에 포함함
        List<Comment> comments = project.getComments();
        List<CommentOutputDTO> commentOutputDTOS = new ArrayList<>(); // Comments를 정리하여 CommentOutputDTO로 변환
        for(Comment comment : comments){
            if(comment.getParentComment()!=null){
                continue;
            }

            // 부모 댓글을 CommentDTO로 변환
            CommentDTO parentComment = CommentDTO.builder()
                    .id(comment.getId())
                    .writer(comment.getWriter().getNickname())
                    .content(comment.getContent())
                    .createdLocalDateTime(comment.getCreatedDateTime())
                    .profileImage(comment.getWriter().getProfileImg())
                    .build();

            CommentOutputDTO commentOutputDTO = new CommentOutputDTO(parentComment);

            // 자식 댓글을 CommentDTO로 변환후 CommentOutputDTO의 childComments에 추가
            List<Comment> childComments = comment.getChildComments();
            for(Comment childComment : childComments){
                CommentDTO childCommentDTO = CommentDTO.builder()
                        .id(childComment.getId())
                        .writer(childComment.getWriter().getNickname())
                        .content(childComment.getContent())
                        .createdLocalDateTime(childComment.getCreatedDateTime())
                        .profileImage(comment.getWriter().getProfileImg())
                        .build();
                commentOutputDTO.addChildComment(childCommentDTO);
            }
            commentOutputDTOS.add(commentOutputDTO);
        }
        return commentOutputDTOS;
    }
}
