package com.sipc.mmtbackend.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.03
 */
public class TimeTransUtil {

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private final static DateTimeFormatter DataDashboardDateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.CHINA);

    private final static DateTimeFormatter TranStringToTimeNotS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.CHINA);
    public static LocalDateTime transStringToTime(String time) {
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

    public static String transStringToTimeDataDashboard(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return localDateTime.format(dateTimeFormatter);
        }
    }

    public static String tranStringToTimeNotS(LocalDateTime localDateTime) {
        return localDateTime.format(TranStringToTimeNotS);
    }

    public static LocalDateTime transLongToTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.of("+8"));
    }

}
