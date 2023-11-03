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
import java.util.stream.IntStream;

import kiss.I;
import viewtify.model.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UICheckBox;
import viewtify.ui.UICheckSwitch;
import viewtify.ui.UIColorPicker;
import viewtify.ui.UIComboBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.util.FXUtils;

public class CalendarSettingView extends View {

    private UIComboBox<DayOfWeek> firstDoW;

    private UIComboBox<LocalTime> startTime;

    private UIComboBox<LocalTime> endTime;

    private UIComboBox<Class> initialView;

    private UICheckSwitch emphsizeToday;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("First day of week"), firstDoW);
                    form(en("Start time"), startTime);
                    form(en("End time"), endTime);
                    form(en("Initial page"), initialView);
                    form(en("Emphsize today"), FormStyles.InputMin, emphsizeToday);
                    form(en("Event sources"), () -> {
                        $(vbox, () -> {
                            for (TimeEventSource source : I.find(TimeEventSource.class)) {
                                $(new TimeEventSourceView(source));
                            }
                        });
                    });
                });
            }
        };

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        firstDoW.items(DayOfWeek.values()).sync(Calendars.setting.firstDoW).renderByVariable(x -> en(x.name()));
        startTime.items(IntStream.range(0, 19).mapToObj(hour -> LocalTime.of(hour, 0))).sync(Calendars.setting.startTime);
        endTime.items(IntStream.range(5, 24).mapToObj(hour -> LocalTime.of(hour, 59))).sync(Calendars.setting.endTime);
        initialView.items(YearView.class, MonthView.class, WeekView.class, DayView.class)
                .style(FormStyles.Input)
                .sync(Calendars.setting.initialView)
                .renderByVariable(x -> en(x.getSimpleName().replace("View", "")));
        emphsizeToday.sync(Calendars.setting.emphsizeToday);
    }

    /**
     * Setting view for {@link TimeEventSource}.
     */
    private static class TimeEventSourceView extends View {

        /** The associtated model. */
        final TimeEventSource source;

        UICheckBox enable;

        UIColorPicker color;

        TimeEventSourceView(TimeEventSource source) {
            this.source = source;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(hbox, FormStyles.Row, () -> {
                        $(enable, FormStyles.InputMin);
                        $(color, FormStyles.Input);
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            TimeEventSourceSetting setting = Preferences.of(TimeEventSourceSetting.class, source.name());

            enable.text(source.name()).sync(setting.enable);
            color.disableWhen(enable.isNotSelected()).sync(setting.color, FXUtils::color, FXUtils::color);
        }
    }
}
