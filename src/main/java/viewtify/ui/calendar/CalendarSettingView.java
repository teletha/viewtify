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

    private UICheckBox emphsizeToday;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("First day of week")), FormStyles.FormLabel);
                        $(firstDoW, FormStyles.FormInput);
                    });
                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("Start time")), FormStyles.FormLabel);
                        $(startTime, FormStyles.FormInput);
                    });
                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("End time")), FormStyles.FormLabel);
                        $(endTime, FormStyles.FormInput);
                    });
                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("Initial page")), FormStyles.FormLabel);
                        $(initialView, FormStyles.FormInput);
                    });
                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("Emphsize today")), FormStyles.FormLabel);
                        $(emphsizeToday, FormStyles.FormInput);
                    });

                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("Event sources")), FormStyles.FormLabel);
                        $(vbox, () -> {
                            I.find(TimeEventSource.class).forEach(source -> {
                                $(new TimeEventSourceView(source));
                            });
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
                    $(hbox, FormStyles.FormRow, () -> {
                        $(enable, FormStyles.FormInputMin);
                        $(color, FormStyles.FormInputMin);
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

    /**
     * Preference for calendar.
     */
    public static class CalendarSetting extends Preferences {

        /** The first day of week. */
        public final Preference<DayOfWeek> firstDoW = initialize(DayOfWeek.SUNDAY);

        /** The start time of day. */
        public final Preference<LocalTime> startTime = initialize(LocalTime.MIN);

        /** The end time of day. */
        public final Preference<LocalTime> endTime = initialize(LocalTime.MAX);

        /** The initial view. */
        public final Preference<Class> initialView = initialize(MonthView.class);

        /** The today's style. */
        public final Preference<Boolean> emphsizeToday = initialize(true);
    }

    /**
     * Preference for calendar.
     */
    public static class TimeEventSourceSetting extends Preferences {

        /** The user defined name. */
        public final Preference<String> name = initialize("");

        /** The availability. */
        public final Preference<Boolean> enable = initialize(true);

        /** The user defined color. */
        public final Preference<stylist.value.Color> color = initialize(stylist.value.Color.Transparent);
    }
}
