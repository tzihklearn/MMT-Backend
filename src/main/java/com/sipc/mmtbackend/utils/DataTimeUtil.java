package com.sipc.mmtbackend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间相关工具封装
 *
 * @author DoudiNCer
 */
public class DataTimeUtil {
    // 东八区时间
    private static final Locale defaultLocale = Locale.SIMPLIFIED_CHINESE;
    private static final String chartAbscissa = "Y.d";

    /**
     * 格式化时间戳通用封装
     *
     * @param timestamp 毫秒级时间戳
     * @param pattern 格式化模板，如“yyyy-MM-dd HH:mm:ss”
     * @return 格式化后的字符串
     * @exception IllegalArgumentException if the given pattern is invalid
     */
    private static String convertTimestamp2Date(Long timestamp, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, defaultLocale);
        return simpleDateFormat.format(new Date(timestamp));
    }

    /**
     * 获取 B 端数据看板折线图横坐标时间
     * @param timestamp 精确到毫秒的时间戳
     * @return 转换后的时间字符串
     */
    public static String getLineChartAbscissaByTimestampS(long timestamp){
        return convertTimestamp2Date(timestamp, chartAbscissa);
    }
}
