package sequence.sequence_member.member.service;

import java.util.ArrayList;
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


    // 모든 스킬 카테고리들을 반환
    public List<String> getAllSkills(){
        List<String> skillNames = new ArrayList<>();

        for(Skill skill : Skill.values()){
            skillNames.add(skill.getName());
        }

        return skillNames;
    }
}
