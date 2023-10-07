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

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import kiss.Extensible;
import kiss.I;
import kiss.Signal;
import viewtify.model.Preferences;
import viewtify.ui.calendar.CalendarSettingView.TimeEventSourceSetting;

public interface TimeEventSource extends Extensible {

    /**
     * The name of this event source.
     * 
     * @return
     */
    default String name() {
        return getClass().getSimpleName();
    }

    /**
     * The availability of this event source.
     * 
     * @return
     */
    default boolean isEnabled() {
        return Preferences.of(TimeEventSourceSetting.class, name()).enable.v;
    }

    /**
     * The color type.
     * 
     * @return
     */
    default Color color() {
        return Preferences.of(TimeEventSourceSetting.class, name()).color.v;
    }

    /**
     * Query events by starting and ending date.
     * 
     * @param start
     * @param end
     * @return
     */
    Signal<TimeEvent> query(LocalDate start, LocalDate end);

    /**
     * Query events by year.
     * 
     * @param year
     * @return
     */
    default Signal<TimeEvent> queryByYear(int year) {
        return queryBy(Year.of(year));
    }

    /**
     * Query events by year.
     * 
     * @param year
     * @return
     */
    default Signal<TimeEvent> queryBy(Year year) {
        return query(year.atDay(1), year.atDay(year.length()));
    }

    /**
     * Query events by month.
     * 
     * @param year
     * @param month
     * @return
     */
    default Signal<TimeEvent> queryByMonth(int year, int month) {
        return queryBy(YearMonth.of(year, month));
    }

    /**
     * Query events by year.
     * 
     * @param yearMonth
     * @return
     */
    default Signal<TimeEvent> queryBy(YearMonth yearMonth) {
        return query(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    /**
     * Query events by day.
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    default Signal<TimeEvent> queryByDay(int year, int month, int day) {
        return queryBy(LocalDate.of(year, month, day));
    }

    /**
     * Query events by day.
     * 
     * @param day
     * @return
     */
    default Signal<TimeEvent> queryBy(LocalDate day) {
        return query(day, day);
    }

    /**
     * Query all events.
     * 
     * @return
     */
    default Signal<TimeEvent> queryAll() {
        return query(LocalDate.MIN, LocalDate.MAX);
    }

    /**
     * Count the number of events.
     * 
     * @param day
     * @return
     */
    Signal<Integer> countOn(LocalDate day);

    /**
     * Group all events by date.
     * 
     * @return
     */
    static Signal<TimeEvent> all() {
        return I.signal(I.find(TimeEventSource.class)).flatMap(TimeEventSource::queryAll);
    }

    /**
     * Group all events by date.
     * 
     * @return
     */
    static Map<LocalDate, List<TimeEvent>> groupByStartDate() {
        return all().toGroup(TimeEvent::startDate);
    }
}
