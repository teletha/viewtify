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

    private int weekId;

    private int dayId;

    private UIVBox box;

    private UIScrollPane scroll;

    private UIVBox pane;

    UILabel day;

    DayCell(int weekId, int dayId) {
        this.weekId = weekId;
        this.dayId = dayId;
    }

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(box, weekId == 0 ? dayId % 7 == 0 ? Styles.firstDay : Styles.firstWeek
                        : dayId % 7 == 0 ? Styles.firstDayOfWeek : Styles.day, () -> {
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
        Style base = () -> {
            display.width.fill();
        };

        Style day = base.with(() -> {
            border.right.width(1, px).color("-fx-outer-border").solid();
            border.bottom.width(1, px).color("-fx-outer-border").solid();
        });

        Style firstDay = base.with(() -> {
            border.width(1, px).color("-fx-outer-border").solid();
        });

        Style firstWeek = base.with(() -> {
            border.right.width(1, px).color("-fx-outer-border").solid();
            border.bottom.width(1, px).color("-fx-outer-border").solid();
            border.top.width(1, px).color("-fx-outer-border").solid();
        });

        Style firstDayOfWeek = base.with(() -> {
            border.right.width(1, px).color("-fx-outer-border").solid();
            border.bottom.width(1, px).color("-fx-outer-border").solid();
            border.left.width(1, px).color("-fx-outer-border").solid();
        });

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

    <T extends TimeEventVisualizer<TimeEvent> & Extensible> void add(TimeEvent event, Class<T> visualizerType) {
        T visualizer = I.find(visualizerType, event.getClass());

        pane.ui.getChildren().add(visualizer.visualize(event).ui());
    }

    /**
     * 
     */
    void markAsToday() {
        box.style(TemporalStyles.today);
    }
}