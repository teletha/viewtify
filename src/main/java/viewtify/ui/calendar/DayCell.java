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
import java.time.LocalTime;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import kiss.Extensible;
import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.ui.UILabel;
import viewtify.ui.UIScrollPane;
import viewtify.ui.UIVBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.calendar.TemporalView.TemporalStyles;
import viewtify.ui.helper.User;

public class DayCell extends View {

    private UIVBox box;

    private UIScrollPane scroll;

    private UIVBox pane;

    UILabel day;

    private LocalTime startTime = Calendars.setting.startTime.or(LocalTime.MIN);

    private long latestUsedTime = startTime.toSecondOfDay();

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(box, Styles.day, () -> {
                    $(hbox, Styles.row, () -> {
                        $(day, Styles.num);
                    });
                    $(scroll, () -> {
                        $(pane);
                    });
                });
            }
        };
    }

    interface Styles extends StyleDSL {
        Style day = () -> {
            display.width.fill();
            border.width(1, px).color("-fx-outer-border").solid();
        };

        Style row = () -> {
            display.width.fill();
            text.align.center();
        };

        Style num = () -> {
            display.width(50, px);
            padding.vertical(3, px);
            text.align.center();
            cursor.pointer();

            $.hover(() -> {
                text.decoration.underline();
            });
        };
    }

    @Override
    protected void initialize() {
        scroll.policy(ScrollBarPolicy.NEVER, ScrollBarPolicy.AS_NEEDED).fit(true, true).thin();
    }

    void set(LocalDate date, int month, CalendarView calendar) {
        box.style(date.getMonthValue() != month, TemporalStyles.outOfMonth);
        day.text(date.getDayOfMonth());

        day.when(User.LeftClick, () -> {
            calendar.show(DayView.class, date);
        });
    }

    /**
     * Add new time event.
     * 
     * @param <T>
     * @param event
     * @param visualizerType
     * @param enableTimeGap
     */
    <T extends TimeEventVisualizer<TimeEvent> & Extensible> void add(TimeEvent event, Class<T> visualizerType, boolean enableTimeGap) {
        LocalTime startTime = event.startTime();
        if (Calendars.isAcceptable(startTime)) {
            T visualizer = I.find(visualizerType, event.getClass());
            Node ui = visualizer.visualize(event).ui();

            if (enableTimeGap) {
                int minHeight = Calendars.setting.eventHeight.v;
                long interval = Math.max(0, (startTime.toSecondOfDay() - latestUsedTime) / 60 * minHeight / 60 * 2);
                ui.setStyle("-fx-margin: " + interval + " 0 0 0; -fx-min-height: " + minHeight + ";");
                latestUsedTime = event.endTime().toSecondOfDay();
            }

            pane.ui.getChildren().add(ui);
        }
    }

    /**
     * 
     */
    void markAsToday() {
        box.style(TemporalStyles.today);
    }
}