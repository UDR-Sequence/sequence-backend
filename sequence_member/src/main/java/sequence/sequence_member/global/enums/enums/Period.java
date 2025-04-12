package sequence.sequence_member.global.enums.enums;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public enum Period {
    ONE_MONTH_LESS,
    ONE_TO_THREE_MONTH,
    THREE_TO_SIX_MONTH,
    SIX_TO_ONE_YEAR,
    OVER_ONE_YEAR;

    public static Period calculatePeriod(String startDateStr, String endDateStr) {
        // yyyy-MM 형식 파서
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // 먼저 YearMonth로 파싱한 후, LocalDate로 변환
        YearMonth startYm = YearMonth.parse(startDateStr, formatter);
        YearMonth endYm = YearMonth.parse(endDateStr, formatter);

        LocalDate startDate = startYm.atDay(1);
        LocalDate endDate = endYm.atDay(1);

        long diffDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (diffDays < 30) {
            return Period.ONE_MONTH_LESS;
        } else if (diffDays < 90) {
            return Period.ONE_TO_THREE_MONTH;
        } else if (diffDays < 180) {
            return Period.THREE_TO_SIX_MONTH;
        } else if (diffDays < 365) {
            return Period.SIX_TO_ONE_YEAR;
        } else {
            return Period.OVER_ONE_YEAR;
        }
    }
}
