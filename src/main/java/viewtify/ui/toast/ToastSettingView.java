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

import kiss.Variable;
import viewtify.prference.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UICheckSwitch;
import viewtify.ui.UIComboBox;
import viewtify.ui.UISpinner;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

public class ToastSettingView extends View {

    /** The desktop configuration UI. */
    private UICheckSwitch enableNotification;

    /** The desktop configuration UI. */
    private UISpinner<Duration> notificationDuration;

    /** The desktop configuration UI. */
    private UIComboBox<ScreenSelector> notificationMonitor;

    /** The desktop configuration UI. */
    private UIComboBox<Corner> notificationArea;

    /** The desktop configuration UI. */
    private UISpinner<Integer> notificationMax;

    /** The desktop configuration UI. */
    private UISpinner<Integer> notificationGap;

    /** The desktop configuration UI. */
    private UISpinner<Integer> notificationOpacity;

    /** Execute notification for test. */
    private UIButton notificationTest;

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable<String> title() {
        return en("Notification");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Enable Notification"), FormStyles.InputMin, enableNotification);
                    form(en("Notification Monitor"), notificationMonitor);
                    form(en("Notification Location"), notificationArea);
                    form(en("Notification Duration"), notificationDuration);
                    form(en("Max number of Notifications"), notificationMax);
                    form(en("Notification area spacing"), notificationGap);
                    form(en("Notification area opacity"), notificationOpacity);
                    form(en("Test notification"), notificationTest);
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

        enableNotification.sync(setting.enable);
        notificationMonitor.items(ScreenSelector.values()).sync(setting.screen).disableWhen(enableNotification.isNotSelected());
        notificationMax.items(0, 1, 2, 3, 4, 5, 6, 7, 8).sync(setting.max).disableWhen(enableNotification.isNotSelected());
        notificationArea.items(Corner.values()).sync(setting.area).disableWhen(enableNotification.isNotSelected());
        notificationDuration.items(IntStream.range(0, 31).mapToObj(x -> Duration.seconds(x * 10)).toList())
                .sync(setting.autoHide)
                .format(d -> String.valueOf((int) d.toSeconds()) + en("sec"))
                .disableWhen(enableNotification.isNotSelected());
        notificationGap.items(IntStream.range(0, 31))
                .sync(setting.gap)
                .format(x -> x + "px")
                .disableWhen(enableNotification.isNotSelected());
        notificationOpacity.items(IntStream.range(0, 101))
                .sync(setting.opacity)
                .format(x -> x + "%")
                .disableWhen(enableNotification.isNotSelected());
        notificationTest.text(en("Notify")).action(() -> Toast.show("Test"));
    }
}
