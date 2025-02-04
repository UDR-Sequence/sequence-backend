package sequence.sequence_member.global.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataConvertor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    //List로 받은 자료형을 DB에 바로 저장하기 위해 String으로 변환할때 사용
    public static <T> String listToString(List<T> data){
        StringBuilder sb = new StringBuilder();
        for (T t : data) {
            sb.append(t).append(",");
        }
        return sb.toString();
    }

    //String으로 db에 저장된 내용을 List로 변환할때 사용
    public static List<String> stringToList(String data){
        // 입력이 비어있거나 빈 배열 형태이면 빈 Set 반환
        if (data == null || data.trim().equals("[]")) {
            return new ArrayList<>();
        }
        return Arrays.asList(data.split("\\s*,\\s*")); //컴마를 기준으로 분리
    }

    // JSON 변환 추가 (객체 → JSON String)
    public static <T> String objectToJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}"; // 변환 실패 시 빈 JSON 객체 반환
        }
    }

    // JSON String → 객체 변환
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null; // 변환 실패 시 null 반환
        }
    }
}


