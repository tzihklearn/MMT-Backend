package com.sipc.mmtbackend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.03
 */
public class TimeTransUtil {

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static LocalDateTime transStringToTime(String time) {
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

}
