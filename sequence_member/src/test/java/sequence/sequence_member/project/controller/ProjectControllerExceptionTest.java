package sequence.sequence_member.project.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.project.service.ProjectBookmarkService;
import sequence.sequence_member.project.service.ProjectService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
public class ProjectControllerExceptionTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProjectBookmarkService projectBookmarkService;

    // GET /api/projects/{projectId} 호출 시 존재하지 않는 프로젝트 404, 에러 메시지 반환 검증
    // CanNotFindResourceException 글로벌 예외 핸들러 처리
    @Test
    public void testGetProjectNotFound() throws Exception {
        // 프로젝트가 존재하지 않는 경우, 서비스에서 예외를 던짐
        when(projectService.getProject(anyLong(), any(HttpServletRequest.class)))
                .thenThrow(new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));

        // /api/projects/1 엔드포인트 호출 후, 404 상태 코드와 에러 메시지 확인
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("해당 프로젝트가 존재하지 않습니다.")));
    }
}
