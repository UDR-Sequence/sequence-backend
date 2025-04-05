package sequence.sequence_member.global.enums.enums;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public enum Period {
    ONE_MONTH_LESS,
    ONE_TO_THREE_MONTH,
    THREE_TO_SIX_MONTH,
    SIX_TO_ONE_YEAR,
    OVER_ONE_YEAR;

    public static Period calculatePeriod(LocalDate startDate, LocalDate endDate) {
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
