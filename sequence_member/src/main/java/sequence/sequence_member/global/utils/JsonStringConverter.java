package sequence.sequence_member.global.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class JsonStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return DataConvertor.objectToJson(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> result = DataConvertor.jsonToObject(dbData, List.class);
        return result != null ? result : Collections.emptyList();
    }
}
