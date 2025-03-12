package sequence.sequence_member.member.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import sequence.sequence_member.global.enums.enums.ProjectRole;

import java.util.ArrayList;
import java.util.List;

public class DesiredJobConverter implements AttributeConverter<List<ProjectRole>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ProjectRole> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSON string", e);
        }
    }

    @Override
    public List<ProjectRole> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ProjectRole>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
