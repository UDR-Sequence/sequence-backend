package sequence.sequence_member.member.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import sequence.sequence_member.global.enums.enums.Skill;

import java.util.ArrayList;
import java.util.List;

@Component
public class SkillCategoryConverter implements AttributeConverter<List<Skill>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Skill> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSON string", e);
        }
    }

    @Override
    public List<Skill> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Skill>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    //요청 받을때 enum으로 변환
    public List<Skill> convertToSkillEnum(List<String> skills){
        List<Skill> convertedSkillEnum = new ArrayList<>();

        for(String skill : skills){
            convertedSkillEnum.add(Skill.fromString(skill));
        }
        return convertedSkillEnum;
    }

    //응답할때 string으로 변환해서 응답
    public List<String> convertToSkillString(List<Skill> skills){
        List<String> convertedSkillString = new ArrayList<>();

        for(Skill skill : skills){
            convertedSkillString.add(Skill.fromSkillEnum(skill));
        }
        return convertedSkillString;
    }
}