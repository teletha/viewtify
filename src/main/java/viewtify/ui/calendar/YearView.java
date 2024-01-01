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
import java.util.Locale;

import javafx.scene.layout.Priority;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.ui.UIGridView;
import viewtify.ui.ViewDSL;

public class YearView extends TemporalView {

    private UIGridView<YearMonthView> box;

    private YearMonthView[] months = new YearMonthView[12];

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(box, 3, 4, months, Styles.box);
            }
        };
    }

    interface Styles extends StyleDSL {

        Style box = () -> {
            display.width.fill().grid().rowGap(25, px).columnGap(50, px);
            padding.size(15, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        box.constrainRow(Priority.ALWAYS)
                .constrainRow(Priority.ALWAYS)
                .constrainRow(Priority.ALWAYS)
                .constrainColumn(Priority.ALWAYS)
                .constrainColumn(Priority.ALWAYS)
                .constrainColumn(Priority.ALWAYS)
                .constrainColumn(Priority.ALWAYS);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                box.ui.add(months[i * 4 + j].ui(), j, i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChronoUnit temporalUnit() {
        return ChronoUnit.YEARS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateTimeFormatter formatter(Locale locale) {
        return Calendars.formatYear(locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(LocalDate date) {
        this.currentDate = date.withMonth(1).withDayOfMonth(1);

        for (int i = 0; i < months.length; i++) {
            months[i].set(currentDate.plusMonths(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(Locale locale) {
        for (YearMonthView month : months) {
            month.set(locale);
        }
    }
}