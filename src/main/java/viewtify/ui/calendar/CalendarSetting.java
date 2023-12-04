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
import java.time.LocalTime;

import viewtify.prference.Preferences;

/**
 * Preference for calendar.
 */
public class CalendarSetting extends Preferences {

    /** The first day of week. */
    public final Preference<DayOfWeek> firstDoW = initialize(DayOfWeek.SUNDAY);

    /** The start time of day. */
    public final Preference<LocalTime> startTime = initialize(LocalTime.MIN);

    /** The end time of day. */
    public final Preference<LocalTime> endTime = initialize(LocalTime.MAX);

    /** The initial view. */
    public final Preference<Class> initialView = initialize(MonthView.class);

    /** The today's style. */
    public final Preference<Boolean> emphsizeToday = initialize(true);

    /** The height of event area. */
    public final Preference<Integer> eventHeight = initialize(38);
}