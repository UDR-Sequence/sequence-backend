package sequence.sequence_member.member.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import sequence.sequence_member.member.entity.EducationEntity;

import java.util.ArrayList;
import java.util.List;

public class DesiredJobConverter implements AttributeConverter<List<EducationEntity>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<EducationEntity> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSON string", e);
        }
    }

    @Override
    public List<EducationEntity> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<EducationEntity>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
