package sequence.sequence_member.member.service;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.enums.enums.Skill;

@Service
@RequiredArgsConstructor
public class SkillService {

    public List<String> getAllSkills(){
        return Arrays.stream(
                Skill.values()).map(Skill::getName)
                .toList();
    }
}
