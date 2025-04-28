package sequence.sequence_member.member.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.enums.enums.Skill;

@Service
@RequiredArgsConstructor
public class SkillService {

    public Map<Skill,String> getAllSkills(){
        return Arrays.stream(Skill.values())
                .collect(Collectors.toMap(skill -> skill, Skill::getName));
    }
}
