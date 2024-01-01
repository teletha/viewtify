/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Locale;

import kiss.I;
import viewtify.Viewtify;
import viewtify.ui.ViewDSL;

public class DayView extends TemporalView {

    private DayCell cell;

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(hbox, TemporalStyles.main, () -> {
                    $(cell);
                });
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChronoUnit temporalUnit() {
        return ChronoUnit.DAYS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateTimeFormatter formatter(Locale locale) {
        return Calendars.formatYearMonthDay(locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(LocalDate date) {
        this.currentDate = date;

        cell.set(date, date.getMonthValue(), calendar);

        Calendars.calculateMark(date, mark -> {
            cell.day.style(mark.style());
        });

        I.signal(I.find(TimeEventSource.class))
                .subscribeOn(Viewtify.WorkerThread)
                .take(TimeEventSource::isEnabled)
                .flatMap(source -> source.queryBy(date))
                .sort(Comparator.naturalOrder())
                .on(Viewtify.UIThread)
                .to(event -> cell.add(event, WeekEventVisualizer.class, true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(Locale locale) {
    }
}