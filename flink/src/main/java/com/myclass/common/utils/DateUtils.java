package com.myclass.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static Long getTimestampFromDateStr(String dateStr, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(dateStr).getTime();
    }

    public static String getDateStrFromTimestamp(Long timestamp, String format) {
        return new SimpleDateFormat(format).format(new Date(timestamp));
    }


}
