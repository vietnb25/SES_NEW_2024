package vn.ses.s3m.plus.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtils {

    /** Logging */
    private static Log log = LogFactory.getLog(DateUtils.class);

    private static final int MINUTE_PARAM = 60;

    private static final int TIME_ZONE_PARAM = 1000;

    /** Convert Timezone. */
    public static Timestamp convertTimezone(final Timestamp inputTime, final int timezone) {

        long time = inputTime.getTime();
        long diffTime = timezone * MINUTE_PARAM * MINUTE_PARAM * TIME_ZONE_PARAM;
        Timestamp newTime = new Timestamp(time + diffTime);

        return newTime;
    }

    /** Return Time Value in Seconds. */
    public static int getTimeInSecond(final String time) {

        String[] timeArr = time.split(Constants.ES.COLON_CHARACTER);
        int hour = Integer.parseInt(timeArr[0]);
        int minute = Integer.parseInt(timeArr[1]);
        int second = Integer.parseInt(timeArr[2]);

        int timeValue = hour * MINUTE_PARAM * MINUTE_PARAM + minute * MINUTE_PARAM + second;

        return timeValue;
    }

    /** Return Format Date String. */
    public static String toString(final Date date, final String format) {

        String sDate;
        DateFormat dateTimeFormat = new SimpleDateFormat(format);
        sDate = dateTimeFormat.format(date);

        return sDate;
    }

    /** Return New Format Date. */
    public static Date toDate(final String sDate, final String format) {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(format);

        try {
            date = dateFormat.parse(sDate);

        } catch (ParseException pe) {
            log.error("Error occured when parsing date format.");
            log.error(pe.getMessage(), pe);
        }

        return date;
    }

    public static String firstDateOfMonth(final String sDate) {
        Calendar cal = Calendar.getInstance();

        Date date = DateUtils.toDate(sDate, Constants.ES.DATE_FORMAT_YMD);
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        String firstDate = DateUtils.toString(cal.getTime(), Constants.ES.DATE_FORMAT_YMD);

        return firstDate;
    }

    public static String lastDateOfMonth(final String sDate) {
        Calendar cal = Calendar.getInstance();

        Date date = DateUtils.toDate(sDate, Constants.ES.DATE_FORMAT_YMD);
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDate = DateUtils.toString(cal.getTime(), Constants.ES.DATE_FORMAT_YMD);

        return lastDate;
    }

    public static int getDayOfWeek(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

}
