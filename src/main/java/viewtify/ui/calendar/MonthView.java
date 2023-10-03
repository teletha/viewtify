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
import java.util.List;
import java.util.Locale;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
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

        I.signal(I.find(TimeEventSource.class))
                .flatMap(source -> source.queryByMonth(date.getYear(), date.getMonthValue()))
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

    private static class MonthDayEntriesPane extends Pane {

        private static final String MONTH_DAY_MORE_LABEL = "more-label";

        private final Label moreLabel;

        private MonthDayEntriesPane(LocalDate date, int week, int day) {
            getStyleClass().add("entries-pane");

            setMinSize(0, 0);
            setPrefSize(0, 0);

            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(widthProperty());
            clip.heightProperty().bind(heightProperty());
            setClip(clip);

            moreLabel = new Label();
            moreLabel.getStyleClass().add(MONTH_DAY_MORE_LABEL);
            moreLabel.setManaged(false);
            moreLabel.setVisible(false);

            getChildren().add(moreLabel);
        }

        @Override
        protected void layoutChildren() {
            Insets insets = getInsets();
            double w = getWidth();
            double h = getHeight();
            double y = insets.getTop();

            moreLabel.setVisible(false);

            List<Node> children = getChildren();

            boolean conflictFound = false;

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                if (child == moreLabel) {
                    continue;
                }
                double ph = child.prefHeight(-1);
                if (y + ph < h - insets.getTop() - insets.getBottom()) {
                    child.resizeRelocate(snapPositionX(insets
                            .getLeft()), snapPositionY(y), snapSizeX(w - insets.getRight() - insets.getLeft()), snapSizeY(ph));

                    y += ph + 1; // +1 = gap
                    child.getProperties().put("hidden", false);
                } else {
                    if (!conflictFound && i > 0) {
                        conflictFound = true;
                        children.get(i - 1).getProperties().put("hidden", true);
                    }

                    child.getProperties().put("hidden", true);
                }
            }

            if (conflictFound) {
                moreLabel.setVisible(true);
                double ph = moreLabel.prefHeight(-1);

                moreLabel.resizeRelocate(snapPositionX(insets.getLeft()), snapPositionY(h - insets.getTop() - insets
                        .getBottom() - ph), snapSizeX(w - insets.getRight() - insets.getLeft()), snapSizeY(ph));
            }
        }
    }
}
