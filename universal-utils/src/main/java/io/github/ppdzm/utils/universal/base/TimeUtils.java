package io.github.ppdzm.utils.universal.base;

import java.time.LocalTime;

/**
 * @author Created by Stuart Alex on 2023/5/12.
 */
public class TimeUtils {

    public static boolean isBetween(LocalTime target, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            return target.isAfter(start) && target.isBefore(end);
        } else if (start.equals(end)) {
            return target.equals(start);
        } else {
            return isBetween(target, end, start);
        }
    }

}
