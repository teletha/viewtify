/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import kiss.I;
import viewtify.prference.Preferences;
import viewtify.ui.calendar.CalendarMarker.Mark;

public class Calendars {

    /** The markers. */
    private static final List<CalendarMarker> markers = I.find(CalendarMarker.class);

    /** The sources. */
    private static final List<TimeEventSource> sources = I.find(TimeEventSource.class);

    /** The singleton accessor. */
    static final CalendarSetting setting = Preferences.of(CalendarSetting.class);

    /**
     * Calculate the marked day.
     * 
     * @param date
     * @param process
     */
    public static void calculateMark(LocalDate date, Consumer<Mark> process) {
        for (CalendarMarker marker : markers) {
            marker.detect(date).ifPresent(process);
        }
    }

    /**
     * Calculate the event size on the specified day.
     * 
     * @param date
     * @param process
     */
    public static void calculateEvents(LocalDate date, IntConsumer process) {
        I.signal(sources)
                .take(TimeEventSource::isEnabled)
                .flatMap(source -> source.countOn(date))
                .map(() -> new int[] {0}, (counter, value) -> counter[0] += value)
                .last()
                .to(process::accept);
    }

    /**
     * Calculate the starting day.
     * 
     * @param date
     * @return
     */
    public static LocalDate calculateStartingMonthDay(LocalDate date) {
        return calculateStartingWeekDay(date.withDayOfMonth(1));
    }

    /**
     * Calculate the starting day.
     * 
     * @param date
     * @return
     */
    public static LocalDate calculateStartingWeekDay(LocalDate date) {
        return date.minusDays((date.getDayOfWeek().getValue() + 7 - setting.firstDoW.v.getValue()) % 7);
    }

    /**
     * @param index
     * @return
     */
    public static DayOfWeek calculateDoW(int index) {
        return setting.firstDoW.v.plus(index);
    }

    /**
     * Test whethrer the given time is acceptable or not.
     * 
     * @param time
     * @return
     */
    public static boolean isAcceptable(LocalTime time) {
        LocalTime start = setting.startTime.or(LocalTime.MIN);
        LocalTime end = setting.endTime.or(LocalTime.MAX);

        return !time.isBefore(start) && !time.isAfter(end);
    }

    /** The localized formatter. */
    static final Map<Locale, DateTimeFormatter> cacheYMD = new ConcurrentHashMap();

    /** The localized formatter. */
    private static final Map<Locale, DateTimeFormatter> cacheYM = new ConcurrentHashMap();

    /** The localized formatter. */
    private static final Map<Locale, DateTimeFormatter> cacheY = new ConcurrentHashMap();

    /**
     * Create the localized {@link DateTimeFormatter}.
     * 
     * @param locale A target locale.
     * @return
     */
    public static DateTimeFormatter formatYearMonthDay(Locale locale) {
        return cacheYMD.computeIfAbsent(locale, key -> {
            return new DateTimeFormatterBuilder().appendLocalized(FormatStyle.LONG, null).toFormatter(key);
        });
    }

    /**
     * Create the localized {@link DateTimeFormatter}.
     * 
     * @param locale A target locale.
     * @return
     */
    public static DateTimeFormatter formatYearMonth(Locale locale) {
        return cacheYM.computeIfAbsent(locale, key -> {
            return new DateTimeFormatterBuilder()
                    .appendPattern(DateTimeFormatterBuilder
                            .getLocalizedDateTimePattern(FormatStyle.LONG, null, Chronology.ofLocale(key), key)
                            .replaceAll("d[日\\-. ]*('[a-z]+' )?", ""))
                    .toFormatter(key);
        });
    }

    /**
     * Create the localized {@link DateTimeFormatter}.
     * 
     * @param locale A target locale.
     * @return
     */
    public static DateTimeFormatter formatYear(Locale locale) {
        return cacheY.computeIfAbsent(locale, key -> {
            return new DateTimeFormatterBuilder()
                    .appendPattern(DateTimeFormatterBuilder
                            .getLocalizedDateTimePattern(FormatStyle.LONG, null, Chronology.ofLocale(key), key)
                            .replaceAll("d[日\\-. ]*('[a-z]+' )?", "")
                            .replaceAll("M+[月\\-., ]*('[a-z]+' )?", ""))
                    .toFormatter(key);
        });
    }

    // public static void main(String[] args) {
    // LocalDate date = LocalDate.now();
    //
    // for (Locale locale : Locale.getAvailableLocales()) {
    // String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.LONG, null,
    // date.getChronology(), locale);
    // System.out.println(locale + " @" + pattern.replaceAll("d[日\\-. ]*('[a-z]+' )?",
    // "").replaceAll("M+[月\\-. ]*('[a-z]+' )?", ""));
    // }
    // }
}
