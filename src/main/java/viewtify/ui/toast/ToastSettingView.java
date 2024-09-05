/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.toast;

import java.util.concurrent.TimeUnit;

import kiss.I;
import kiss.Variable;
import viewtify.preference.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UICheckSwitch;
import viewtify.ui.UIComboBox;
import viewtify.ui.UISlider;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

public class ToastSettingView extends View {

    /** The desktop configuration UI. */
    private UICheckSwitch enableNotification;

    /** The desktop configuration UI. */
    private UISlider notificationDuration;

    /** The desktop configuration UI. */
    private UIComboBox<ScreenSelector> notificationMonitor;

    /** The desktop configuration UI. */
    private UIComboBox<Corner> notificationArea;

    /** The desktop configuration UI. */
    private UISlider notificationMax;

    /** The desktop configuration UI. */
    private UISlider notificationGap;

    /** The desktop configuration UI. */
    private UISlider notificationOpacity;

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
                    form(en("Enable Notification"), FormStyles.Column3, enableNotification);
                    form(en("Notification Monitor"), FormStyles.Column5, notificationMonitor);
                    form(en("Notification Location"), FormStyles.Column5, notificationArea);
                    form(en("Notification Duration"), FormStyles.Column5, notificationDuration);
                    form(en("Max number of Notifications"), FormStyles.Column5, notificationMax);
                    form(en("Notification area spacing"), FormStyles.Column5, notificationGap);
                    form(en("Notification area opacity"), FormStyles.Column5, notificationOpacity);
                    form(en("Test notification"), FormStyles.Column5, notificationTest);
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
        notificationMax.range(0, 10).sync(setting.max).disableWhen(enableNotification.isNotSelected());
        notificationArea.items(Corner.values()).sync(setting.area).disableWhen(enableNotification.isNotSelected());
        notificationDuration.range(0, 300)
                .step(5)
                .sync(setting.autoHide)
                .format(sec -> sec + en("sec"))
                .disableWhen(enableNotification.isNotSelected());
        notificationGap.range(0, 30).sync(setting.gap).format(x -> x + "px").disableWhen(enableNotification.isNotSelected());
        notificationOpacity.range(0, 100).sync(setting.opacity).format(x -> x + "%").disableWhen(enableNotification.isNotSelected());
        notificationTest.text(en("Notify")).action(() -> {

            ToastMonitor monitor = new ToastMonitor().title("This is test process.")
                    .message("Create message.")
                    .whenCanceled(() -> System.out.println("Canceled"));

            Toast.show(monitor);

            I.schedule(100, 100, TimeUnit.MILLISECONDS, true).take(100).to(x -> {
                monitor.progress(x / 100d);
            });
        });
    }
}