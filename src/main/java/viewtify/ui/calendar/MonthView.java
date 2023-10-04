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
import javafx.scene.layout.RowConstraints;
import kiss.I;
import viewtify.Viewtify;
import viewtify.ui.UIGridView;
import viewtify.ui.UILabel;
import viewtify.ui.ViewDSL;

public class MonthView extends TemporalView {

    private UIGridView<?> grid;

    private final UILabel[] dow = new UILabel[7];

    private final DayCell[][] cells = new DayCell[6][7];

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

        LocalDate start = Calendars.calculateStartingMonthDay(currentDate);
        LocalDate end = start.plusDays(35);
        int rows = currentDate.getMonth() == end.getMonth() ? 6 : 5;

        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100d / 7d);
        column.setHgrow(Priority.ALWAYS);
        column.setHalignment(HPos.CENTER);

        RowConstraints row = new RowConstraints();
        row.setPercentHeight(96d / rows);
        row.setVgrow(Priority.SOMETIMES);

        grid.constrainRow(Priority.NEVER).constrain(row, rows).constrain(column, 7);

        for (int i = 0; i < 7; i++) {
            grid.ui.add(dow[i].style(TemporalStyles.rowDayOfWeek).ui(), i, 0);

            for (int j = 0; j < rows; j++) {
                grid.ui.add(cells[j][i].ui(), i, j + 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChronoUnit temporalUnit() {
        return ChronoUnit.MONTHS;
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
        this.currentDate = date.withDayOfMonth(1);

        LocalDate start = Calendars.calculateStartingMonthDay(currentDate);
        LocalDate end = start.plusDays(41);

        LocalDate processing = start;
        for (DayCell[] weeks : cells) {
            for (DayCell dayCell : weeks) {
                dayCell.set(processing, date.getMonthValue(), calendar);

                Calendars.calculateMark(processing, mark -> {
                    dayCell.day.style(mark.style());
                });

                processing = processing.plusDays(1);
            }
        }

        if (Calendars.setting.emphsizeToday.is(true)) {
            int diff = (int) (LocalDate.now().toEpochDay() - start.toEpochDay());
            if (0 <= diff && diff < currentDate.lengthOfMonth()) {
                cells[diff / 7][diff % 7].markAsToday();
            }
        }

        I.signal(I.find(TimeEventSource.class))
                .flatMap(source -> source.query(start, end))
                .sort(Comparator.naturalOrder())
                .on(Viewtify.UIThread)
                .to(event -> {
                    long index = event.startDate().toEpochDay() - start.toEpochDay();
                    int row = (int) index / 7;
                    int column = (int) index % 7;
                    cells[row][column].add(event, MonthEventVisualizer.class);
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
