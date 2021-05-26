package io.github.ppdzm.utils.universal.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Created by Stuart Alex on 2021/5/19.
 */
public class DateTimeUtils {
    private static Map<String, SimpleDateFormat> dataFormats = new HashMap<>();
    private static List<String> zoneIds = Arrays.asList(
        "GMT", "GMT+01:00", "GMT+02:00", "GMT+03:00", "GMT+03:30", "GMT+04:00", "GMT+05:00", "GMT+05:30", "GMT+06:00",
        "GMT+07:00", "GMT+08:00", "GMT+09:00", "GMT+09:30", "GMT+10:00", "GMT+11:00", "GMT+12:00",
        "GMT-11:00", "GMT-10:00", "GMT-09:00", "GMT-08:00", "GMT-07:00", "GMT-06:00", "GMT-05:00", "GMT-04:00", "GMT-03:30", "GMT-03:00", "GMT-01:00"
    );

    /**
     * 比较不同格式的时区字符串
     *
     * @param timezone1 时区字符串
     * @param timezone2 时区字符串
     * @return 比较结果
     */
    public static boolean compareTimezone(String timezone1, String timezone2) {
        return timezone1 != null && timezone2 != null && timezone1.startsWith("GMT") && timezone2.startsWith("GMT") &&
            timezone2.startsWith("GMT") && TimeZone.getTimeZone(timezone1).getID().equals(TimeZone.getTimeZone(timezone2).getID());
    }

    public static List<String> dateRange(String dt, String format) throws ParseException {
        List<String> dateRange = new ArrayList<>();
        if (dt.contains(",")) {
            // 离散的日期
            dateRange = Arrays.asList(dt.split(","));
        } else if (dt.contains("-")) {
            // 范围的日期
            String[] splits = dt.split("-");
            String startDay, endDay;
            if (splits.length == 1) {
                startDay = splits[0];
                endDay = yesterday(format);
            } else {
                startDay = splits[0];
                endDay = splits[1];
            }
            String cursor = startDay;
            while (Integer.parseInt(cursor) <= Integer.parseInt(endDay)) {
                dateRange.add(cursor);
                cursor = dayAfterSomeDay(cursor, 1, format);
            }
        } else {
            // 单一的日期
            dateRange.add(dt);
        }
        return dateRange;
    }

    public static String dayAfterSomeDay(String someDay, int number, String format) throws ParseException {
        return dayBeforeSomeDay(someDay, -number, format);
    }

    public static String dayBeforeSomeDay(String someDay, int number, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = getDateFormat(format);
        long someDayDate = simpleDateFormat.parse(someDay).getTime();
        long before = someDayDate - number * 3600 * 1000 * 24;
        return simpleDateFormat.format(new Date(before));
    }

    public static String yesterday(String format) {
        return dayBeforeToday(1, format);
    }

    public static String dayBeforeToday(int number, String format) {
        return hourBeforeNow(number * 24, format);
    }

    public static String hourBeforeNow(int number, String format) {
        long now = System.currentTimeMillis();
        long before = now - number * 3600 * 1000;
        return getDateFormat(format).format(new Date(before));
    }

    public static SimpleDateFormat getDateFormat(String format) {
        if (!dataFormats.containsKey(format)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            dataFormats.put(format, simpleDateFormat);
        }
        return dataFormats.get(format);
    }

    public static String dayOfLastHour(String format) {
        return hourBeforeNow(1, format);
    }

    public static String format(Date date, String format) {
        return getDateFormat(format).format(date);
    }

    public static String format(long unixTime, String format) throws Exception {
        return getDateFormat(format).format(unixTime2Date(unixTime));
    }

    public static Date unixTime2Date(long unixTime) throws Exception {
        int length = String.valueOf(unixTime).length();
        switch (length) {
            case 10:
                return new Date(unixTime * 1000);
            case 13:
                return new Date(unixTime);
            case 16:
                return new Date(unixTime / 1000);
            default:
                throw new Exception("cannot determine time unit of " + unixTime);
        }
    }

    public static String getCurrentDate(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getDateOfDaysBefore(int beforeDays, String pattern) {
        return LocalDate.now().minusDays(beforeDays).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String lastHour(String format) {
        return hourBeforeNow(1, format);
    }

    /**
     * 返回在这个时间范围内的timezone
     *
     * @param startHourMinute HH:mm
     * @param endHourMinute   HH:mm
     * @return list of matched zone ids
     */
    public static List<String> matchedZoneIds(String startHourMinute, String endHourMinute) {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<String> matchedZoneIds = new ArrayList<>();
        for (String zoneId : zoneIds) {
            String zoneTimeHourMin = now.atZone(ZoneId.of(zoneId)).format(formatter);
            if (zoneTimeHourMin.compareTo(startHourMinute) > 0 && zoneTimeHourMin.compareTo(endHourMinute) < 0) {
                matchedZoneIds.add(zoneId);
            }
        }
        return matchedZoneIds;
    }

    public static String nowWithFormat(String format) {
        return getDateFormat(format).format(new Date(System.currentTimeMillis()));
    }

    public static String secondsToTime(int seconds) {
        int h = (seconds / 60 / 60) % 24;
        int m = (seconds / 60) % 60;
        int s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String unixTime2Datetime(long unixTime) {
        LocalDateTime date = Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }
}
