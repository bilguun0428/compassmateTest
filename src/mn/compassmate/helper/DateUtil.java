package mn.compassmate.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeParser();

    private DateUtil() {
    }

    public static Date correctDay(Date guess) {
        return correctDate(new Date(), guess, Calendar.DAY_OF_MONTH);
    }

    public static Date correctYear(Date guess) {
        return correctDate(new Date(), guess, Calendar.YEAR);
    }

    public static Date correctDate(Date now, Date guess, int field) {

        if (guess.getTime() > now.getTime()) {
            Date previous = dateAdd(guess, field, -1);
            if (now.getTime() - previous.getTime() < guess.getTime() - now.getTime()) {
                return previous;
            }
        } else if (guess.getTime() < now.getTime()) {
            Date next = dateAdd(guess, field, 1);
            if (next.getTime() - now.getTime() < now.getTime() - guess.getTime()) {
                return next;
            }
        }

        return guess;
    }

    private static Date dateAdd(Date guess, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(guess);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static Date parseDate(String value) {
        return DATE_FORMAT.parseDateTime(value).toDate();
    }

    public static String getWeekDayName(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return simpleDateFormat.format(date);
    }

    public static Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
}
