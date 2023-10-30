/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.toast;

import java.util.stream.IntStream;

import javafx.util.Duration;
import viewtify.model.Preferences;
import viewtify.ui.UICheckBox;
import viewtify.ui.UIComboBox;
import viewtify.ui.UISpinner;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

public class ToastSettingView extends View {

    /** The desktop configuration UI. */
    private UICheckBox enableNotification;

    /** The desktop configuration UI. */
    private UISpinner<Duration> notificationDuration;

    /** The desktop configuration UI. */
    private UIComboBox<ScreenSelector> notificationMonitor;

    /** The desktop configuration UI. */
    private UIComboBox<Corner> notificationArea;

    /** The desktop configuration UI. */
    private UISpinner<Integer> notificationMax;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Notification Monitor"), notificationMonitor);
                    form(en("Notification Location"), notificationArea);
                    form(en("Notification Duration"), notificationDuration);
                    form(en("Max number of Notifications"), notificationMax);
                });
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        ToastSetting setting = Preferences.of(ToastSetting.class);

        enableNotification.text(en("Enable notification")).sync(setting.enable);
        notificationMonitor.items(ScreenSelector.values()).sync(setting.screen);
        notificationMax.items(0, 1, 2, 3, 4, 5, 6, 7, 8).sync(setting.max).disableWhen(enableNotification.isNotSelected());
        notificationArea.items(Corner.values()).sync(setting.area).disableWhen(enableNotification.isNotSelected());
        notificationDuration.items(IntStream.range(0, 21).mapToObj(x -> Duration.seconds(x * 15)).toList())
                .sync(setting.autoHide)
                .format(d -> String.valueOf((int) d.toSeconds()) + en("sec"))
                .disableWhen(enableNotification.isNotSelected());
    }
}
