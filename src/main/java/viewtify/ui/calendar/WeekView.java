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
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Locale;

import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import kiss.I;
import viewtify.Viewtify;
import viewtify.ui.UIGridView;
import viewtify.ui.UILabel;
import viewtify.ui.ViewDSL;

public class WeekView extends TemporalView {

    private UIGridView<?> grid;

    private final UILabel[] dow = new UILabel[7];

    private final DayCell[] cells = new DayCell[7];

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {

                $(grid, TemporalStyles.main);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        ColumnConstraints constraint = new ColumnConstraints();
        constraint.setPercentWidth(14.2857143);
        constraint.setHgrow(Priority.ALWAYS);
        constraint.setHalignment(HPos.CENTER);

        grid.constrainRow(Priority.NEVER)
                .constrainRow(Priority.ALWAYS)
                .constrain(constraint)
                .constrain(constraint)
                .constrain(constraint)
                .constrain(constraint)
                .constrain(constraint)
                .constrain(constraint)
                .constrain(constraint);

        for (int i = 0; i < 7; i++) {
            grid.ui.add(dow[i].style(TemporalStyles.rowDayOfWeek).ui(), i, 0);
            grid.ui.add(cells[i].ui(), i, 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChronoUnit temporalUnit() {
        return ChronoUnit.WEEKS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateTimeFormatter formatter(Locale locale) {
        return Calendars.formatYearMonth(locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(LocalDate date) {
        this.currentDate = date;

        LocalDate start = Calendars.calculateStartingWeekDay(date);
        LocalDate end = start.plusDays(6);

        LocalDate processing = start;

        for (DayCell cell : cells) {
            cell.set(processing, date.getMonthValue(), calendar);

            Calendars.calculateMark(processing, mark -> {
                cell.day.style(mark.style());
            });

            processing = processing.plusDays(1);
        }

        if (Calendars.setting.emphsizeToday.is(true)) {
            int diff = (int) (LocalDate.now().toEpochDay() - start.toEpochDay());
            if (0 <= diff && diff <= 6) {
                cells[diff].markAsToday();
            }
        }

        I.signal(I.find(TimeEventSource.class))
                .flatMap(source -> source.query(start, end))
                .sort(Comparator.naturalOrder())
                .on(Viewtify.UIThread)
                .to(event -> {
                    long index = event.startDate().toEpochDay() - start.toEpochDay();
                    cells[(int) index].add(event, WeekEventVisualizer.class);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(Locale locale) {
        for (int i = 0; i < dow.length; i++) {
            dow[i].text(Calendars.calculateDoW(i).getDisplayName(TextStyle.SHORT_STANDALONE, locale));
        }
    }
}
