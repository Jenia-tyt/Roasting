package com.friendandco.roasting.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIMER_FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    public static String today() {
        LocalDate localDate = LocalDate.now();
        return localDate.format(DATE_FORMATTER);
    }

    public static String getTimerTime(LocalTime time) {
        return time.format(TIMER_FORMATTER);
    }
}
