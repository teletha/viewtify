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

import viewtify.model.PreferenceModel;
import viewtify.style.FormStyles;
import viewtify.ui.UICheckBox;
import viewtify.ui.UIComboBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class CalendarSettingView extends View {

    private UIComboBox<DayOfWeek> firstDoW;

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
                        label((en("Initial page")), FormStyles.FormLabel);
                        $(initialView, FormStyles.FormInput);
                    });
                    $(hbox, FormStyles.FormRow, () -> {
                        label((en("Emphsize today")), FormStyles.FormLabel);
                        $(emphsizeToday, FormStyles.FormInput);
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
        initialView.items(YearView.class, MonthView.class, WeekView.class, DayView.class)
                .sync(Calendars.setting.initialView)
                .renderByVariable(x -> en(x.getSimpleName().replace("View", "")));
        emphsizeToday.sync(Calendars.setting.emphsizeToday);
    }

    /**
     * Preference for calendar.
     */
    public static class Setting extends PreferenceModel<Setting> {

        /** The first day of week. */
        public final Preference<DayOfWeek> firstDoW = initialize(DayOfWeek.SUNDAY);

        /** The initial view. */
        public final Preference<Class> initialView = initialize(MonthView.class);

        /** The today's style. */
        public final Preference<Boolean> emphsizeToday = initialize(true);

        /**
         * Hide constructor.
         */
        private Setting() {
            sync();
        }
    }
}
