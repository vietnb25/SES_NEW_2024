package vn.ses.s3m.plus.common;

public class CommonUtils {

    public static int[] calculateDataIndex(String fromDate, String toDate) {
        int[] duration = new int[2];

        long from = DateUtils.toDate(fromDate, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;
        long to = DateUtils.toDate(toDate, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;

        if (from <= to) {
            for (int i = 0; i < Constants.DATA.times.length; i++) {
                if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                    duration[0] = i + 1;
                }

                if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                    duration[1] = i + 1;
                }
            }
        }

        return duration;
    }

    public static int calculateDataIndex(String date) {
        int tableIndex = 0;

        if (date.indexOf(Constants.ES.SLASH_CHARACTER) > 0) {
            String[] dates = date.split(Constants.ES.SLASH_CHARACTER);
            date = dates[2] + "-" + dates[1] + "-" + dates[0];
        }

        long timestamp = DateUtils.toDate(date + " 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;
        for (int i = 0; i < Constants.DATA.times.length; i++) {
            if (Long.compare(timestamp, Constants.DATA.times[i]) > 0) {
                tableIndex = i + 1;
            }
        }

        return tableIndex;
    }

}
