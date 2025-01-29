package sequence.sequence_member.global.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataConvertor {

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
}
