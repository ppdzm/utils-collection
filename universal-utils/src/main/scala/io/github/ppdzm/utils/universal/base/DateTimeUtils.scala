package io.github.ppdzm.utils.universal.base

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneId}
import java.util.{Date, TimeZone}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * @author StuartAlex on 2019-07-26 14:18
 */
object DateTimeUtils {
    private val dataFormats = mutable.Map[String, SimpleDateFormat]()
    private val zoneIds = List("GMT", "GMT+01:00", "GMT+02:00", "GMT+03:00", "GMT+03:30", "GMT+04:00", "GMT+05:00", "GMT+05:30", "GMT+06:00",
        "GMT+07:00", "GMT+08:00", "GMT+09:00", "GMT+09:30", "GMT+10:00", "GMT+11:00", "GMT+12:00",
        "GMT-11:00", "GMT-10:00", "GMT-09:00", "GMT-08:00", "GMT-07:00", "GMT-06:00", "GMT-05:00", "GMT-04:00", "GMT-03:30", "GMT-03:00", "GMT-01:00")

    /**
     * 比较不同格式的时区字符串
     *
     * @param timezone1 时区字符串
     * @param timezone2 时区字符串
     * @return 比较结果
     */
    def compareTimezone(timezone1: String, timezone2: String): Boolean = {
        timezone1 != null && timezone2 != null && timezone1.startsWith("GMT") && timezone2.startsWith("GMT") &&
            timezone2.startsWith("GMT") && TimeZone.getTimeZone(timezone1).getID == TimeZone.getTimeZone(timezone2).getID
    }

    def dateRange(dt: String, format: String = "yyyyMMdd"): List[String] = {
        val (startDay, endDay) = if (dt.contains("-")) {
            // 处理多天的情况
            val splits = dt.split("-")
            if (splits.length == 1) {
                (splits(0), yesterday())
            } else {
                (splits(0), splits(1))
            }
        } else {
            (dt, dt)
        }
        val dateRange = new ListBuffer[String]()
        var cursor = startDay
        while (cursor <= endDay) {
            dateRange += cursor
            cursor = dayAfterSomeDay(cursor, 1, format)
        }
        dateRange.toList
    }

    def dayAfterSomeDay(someDay: String, number: Int, format: String = "yyyyMMdd"): String = dayBeforeSomeDay(someDay, -number, format)

    def dayBeforeSomeDay(someDay: String, number: Int, format: String = "yyyyMMdd"): String = {
        val simpleDateFormat = getDateFormat(format)
        val someDayDate = simpleDateFormat.parse(someDay).getTime
        val before = someDayDate - number * 3600 * 1000 * 24
        simpleDateFormat.format(new Date(before))
    }

    def yesterday(format: String = "yyyyMMdd"): String = dayBeforeToday(1, format)

    def dayBeforeToday(number: Int, format: String = "yyyyMMdd"): String = hourBeforeNow(number * 24, format)

    def dayOfLastHour(format: String = "yyyyMMdd"): String = hourBeforeNow(1, format)

    def format(date: Date, format: String): String = getDateFormat(format).format(date)

    def format(unixTime: Long, format: String): String = getDateFormat(format).format(unixTime2Date(unixTime))

    def unixTime2Date(unixTime: Long): Date = {
        if (unixTime.toString.length == 13)
            new Date(unixTime)
        else
            new Date(unixTime / 1000)
    }

    def getCurrentDate(pattern: String = "yyyyMMdd"): String = {
        LocalDate.now.format(DateTimeFormatter.ofPattern(pattern))
    }

    def getDateOfDaysBefore(beforeDays: Int, pattern: String = "yyyyMMdd"): String = {
        LocalDate.now.minusDays(beforeDays).format(DateTimeFormatter.ofPattern(pattern))
    }

    def lastHour(format: String = "HH"): String = hourBeforeNow(1, format)

    def hourBeforeNow(number: Int, format: String = "HH"): String = {
        val now = System.currentTimeMillis()
        val before = now - number * 3600 * 1000
        getDateFormat(format).format(new Date(before))
    }

    def getDateFormat(format: String): SimpleDateFormat = {
        if (!dataFormats.contains(format)) {
            val simpleDateFormat = new SimpleDateFormat(format)
            dataFormats.put(format, simpleDateFormat)
        }
        dataFormats(format)
    }

    /**
     * 返回在这个时间范围内的timezone
     *
     * @param startHourMinute HH:mm
     * @param endHourMinute   HH:mm
     * @return list of matched zone ids
     */
    def matchedZoneIds(startHourMinute: String, endHourMinute: String): List[String] = {
        val now = Instant.now
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        zoneIds.filter {
            zoneId =>
                val zoneTimeHourMin = now.atZone(ZoneId.of(zoneId)).format(formatter)
                startHourMinute < zoneTimeHourMin && zoneTimeHourMin < endHourMinute
        }
    }

    def nowWithFormat(format: String): String = {
        getDateFormat(format).format(new Date(System.currentTimeMillis()))
    }

    def secondsToTime(seconds: Long): String = {
        val h = (seconds / 60 / 60) % 24
        val m = (seconds / 60) % 60
        val s = seconds % 60
        "%02d:%02d:%02d".format(h, m, s)
    }

    def unixTime2Datetime(unixTime: Long): String = {
        val date = Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault()).toLocalDateTime
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        date.format(formatter)
    }

}
