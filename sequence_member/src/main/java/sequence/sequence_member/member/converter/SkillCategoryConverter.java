package sequence.sequence_member.member.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import sequence.sequence_member.global.enums.enums.Skill;

import java.util.ArrayList;
import java.util.List;

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
}