package sequence.sequence_member.project.mapper;

import sequence.sequence_member.global.enums.enums.Period;

import java.util.HashMap;
import java.util.Map;

public class PeriodMapper{
    private static Map<String, Period> KeywordToPeriod= new HashMap<>();

    static {
        KeywordToPeriod.put("1개월 이하", Period.ONE_MONTH_LESS);
        KeywordToPeriod.put("1개월 ~ 3개월", Period.ONE_TO_THREE_MONTH);
        KeywordToPeriod.put("3개월 ~ 6개월", Period.THREE_TO_SIX_MONTH);
        KeywordToPeriod.put("6개월 ~ 1년", Period.SIX_TO_ONE_YEAR);
        KeywordToPeriod.put("1년 이상", Period.OVER_ONE_YEAR);
    }

    //유효하지 않은 키워드가 들어올 경우 예외처리
    public static Period PeriodCheck(String keyword){
        //keyword가 없을 경우, period값을 null로 반환하여 인자 값에 비어있는 값이 들어가는 것을 방지
        if(keyword==null) return null;

        Period period = KeywordToPeriod.get(keyword);
        if(period == null){
            throw new IllegalArgumentException("잘못된 키워드입니다: " + keyword);
        }
        return period;
    }

}

