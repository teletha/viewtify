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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.ui.View;
import viewtify.ui.anime.SwapAnime;

public abstract class TemporalView extends View {

    /** The base view. */
    protected CalendarView calendar;

    /** The processing local date. */
    protected LocalDate currentDate;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        I.Lang.observing()
                .map(Locale::forLanguageTag)
                .on(Viewtify.UIThread)
                .effect(this::set)
                .map(this::formatter)
                .map(formatter -> formatter.format(currentDate))
                .to(calendar.current::text, this);
    }

    /**
     * Select next temporal location.
     */
    public final void next() {
        calendar.show(getClass(), currentDate.plus(1, temporalUnit()), SwapAnime.SlideLeft);
    }

    /**
     * Select previous temporal location.
     */
    public final void previous() {
        calendar.show(getClass(), currentDate.minus(1, temporalUnit()), SwapAnime.SlideRight);
    }

    /**
     * Select today.
     */
    public final void today() {
        calendar.show(getClass(), LocalDate.now());
    }

    /**
     * Detect the associated termporal unit.
     * 
     * @return
     */
    protected abstract ChronoUnit temporalUnit();

    /**
     * Find the associated date formatter.
     * 
     * @return
     */
    protected abstract DateTimeFormatter formatter(Locale locale);

    /**
     * Set the specified temporal location.
     * 
     * @param date
     */
    protected abstract void set(LocalDate date);

    /**
     * Set the specified locale.
     * 
     * @param locale
     */
    protected abstract void set(Locale locale);

    /**
     * Common style for calendar.
     */
    protected interface TemporalStyles extends StyleDSL {

        Style main = () -> {
            display.width.fill().height.fill();
        };

        Style dayOfWeek = () -> {
            display.width.fill().height.fill();
            text.align.center();
        };

        Style rowDayOfWeek = () -> {
            display.width.fill();
            padding.vertical(4, px);
        };

        Style row = () -> {
            display.width.fill().height.fill();
        };

        Style outOfMonth = () -> {
            display.opacity(0.66);
        };

        Style today = () -> {
            border.color("-fx-focus-color").width(2, px);
        };
    }
}
