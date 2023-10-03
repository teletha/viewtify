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
import viewtify.ui.UIComboBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class CalendarSettingView extends View {

    private UIComboBox<DayOfWeek> firstDoW;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(hbox, FormStyles.FormRow, () -> {
                    label((en("First day of week")), FormStyles.FormLabel);
                    $(firstDoW, FormStyles.FormInput);
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
    }

    /**
     * Preference for calendar.
     */
    public static class Setting extends PreferenceModel<Setting> {

        /** The first day of week. */
        public final Preference<DayOfWeek> firstDoW = initialize(DayOfWeek.SUNDAY);

        /**
         * Hide constructor.
         */
        private Setting() {
            sync();
        }
    }
}
