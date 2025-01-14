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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Numeric;
import viewtify.ui.UIGridView;
import viewtify.ui.UILabel;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.calendar.TemporalView.TemporalStyles;
import viewtify.ui.helper.User;

public class YearMonthView extends View {

    private UILabel monthText;

    private UILabel[] days = new UILabel[49];

    private UIGridView<?> grid;

    private Month month;

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    $(monthText, Styles.month);
                    $(grid, Styles.root);
                });
            }
        };
    }

    interface Styles extends StyleDSL {

        Numeric dayWidth = Numeric.num(2, em);

        Numeric dayHeight = Numeric.num(2, em);

        Style root = () -> {
            display.width.fill().height.fill().grid().columnGap(2, px).rowGap(2, px);

        };

        Style week = () -> {
            display.width.fill();
        };

        Style month = () -> {
            display.width.fill().height(dayHeight);
            padding.horizontal(dayWidth.divide(3)).bottom(7, px);
            cursor.pointer();
            font.weight.bolder();

            $.hover(() -> {
                text.decoration.underline();
            });
        };

        Style box = () -> {
            display.width(dayWidth).height(dayHeight);
            text.align.center().verticalAlign.middle();
            border.radius(2, px);
        };

        Style day = box.with(() -> {
            cursor.pointer();

            $.hover(() -> {
                text.decoration.underline();
            });
        });

        Style five = () -> {
            background.color("-fx-accent4");
        };

        Style four = () -> {
            background.color("-fx-accent3");
        };

        Style three = () -> {
            background.color("-fx-accent2");
        };

        Style two = () -> {
            background.color("-fx-accent1");
        };

        Style one = () -> {
            background.color("-fx-accent");
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        for (int dow = 0; dow < 7; dow++) {
            grid.ui.add(days[dow].style(Styles.box).ui(), dow, 0);
        }

        for (int week = 1; week < 7; week++) {
            for (int dow = 0; dow < 7; dow++) {
                grid.ui.add(days[week * 7 + dow].ui(), dow, week);
            }
        }

        for (int i = 0; i < 7; i++) {
            ColumnConstraints con = new ColumnConstraints();
            con.setPercentWidth(14.285);
            con.setHalignment(HPos.CENTER);
            grid.ui.getColumnConstraints().add(con);

            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            grid.ui.getRowConstraints().add(row);

            days[i].style(TemporalStyles.outOfMonth);
        }

        monthText.when(User.LeftClick, () -> {
            CalendarView calendar = I.make(CalendarView.class);
            calendar.show(MonthView.class, calendar.currentDate.withMonth(month.getValue()));
        });
    }

    protected void set(LocalDate date) {
        this.month = date.getMonth();

        LocalDate processing = Calendars.calculateStartingMonthDay(date);
        for (int i = 7; i < days.length; i++) {
            UILabel dayCell = days[i];
            if (processing.getMonth() == date.getMonth()) {
                LocalDate associated = processing;

                dayCell.style(Styles.day).text(processing.getDayOfMonth()).when(User.LeftClick).to(e -> {
                    CalendarView calendar = I.make(CalendarView.class);
                    calendar.show(DayView.class, associated);
                });

                Calendars.calculateMark(associated, mark -> {
                    dayCell.style(mark.style());
                });

                Calendars.calculateEvents(associated, size -> {
                    Style numbered = switch (size) {
                    case 0 -> null;
                    case 1 -> Styles.one;
                    case 2 -> Styles.two;
                    case 3 -> Styles.three;
                    case 4 -> Styles.four;
                    default -> Styles.five;
                    };
                    dayCell.style(numbered);
                });

            }

            processing = processing.plusDays(1);
        }

        if (Calendars.setting.emphsizeToday.is(true)) {
            LocalDate today = LocalDate.now();
            if (today.getYear() == date.getYear() && today.getMonth() == month) {
                int diff = (int) (today.toEpochDay() - today.withDayOfMonth(1).toEpochDay());
                days[7 + diff].style(TemporalStyles.today);
            }
        }
    }

    protected void set(Locale locale) {
        for (int i = 0; i < 7; i++) {
            days[i].text(Calendars.calculateDoW(i).getDisplayName(TextStyle.SHORT_STANDALONE, locale));
        }

        monthText.text(month.getDisplayName(TextStyle.FULL_STANDALONE, locale));
    }
}