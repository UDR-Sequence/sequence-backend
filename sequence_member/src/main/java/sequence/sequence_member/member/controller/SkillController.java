package sequence.sequence_member.member.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.enums.enums.Skill;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.service.SkillService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    // 모든 스킬들을 조회하는 컨트롤러
    @GetMapping
    public ResponseEntity<ApiResponseData<Map<Skill,String>>> getAllSkills() {
        Map<Skill, String> skills = skillService.getAllSkills();
        return ResponseEntity.ok(ApiResponseData.success(skills));
    }
}
