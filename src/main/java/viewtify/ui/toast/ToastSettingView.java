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
        notificationMax.range(0, 10).sync(setting.max).disableWhen(enableNotification.isNotSelected());
        notificationArea.items(Corner.values()).sync(setting.area).disableWhen(enableNotification.isNotSelected());
        notificationDuration.range(0, 300)
                .step(5)
                .sync(setting.autoHide)
                .format(sec -> sec + en("sec"))
                .disableWhen(enableNotification.isNotSelected());
        notificationGap.range(0, 30).sync(setting.gap).format(x -> x + "px").disableWhen(enableNotification.isNotSelected());
        notificationOpacity.range(0, 100).sync(setting.opacity).format(x -> x + "%").disableWhen(enableNotification.isNotSelected());
        notificationTest.text(en("Notify")).action(() -> Toast.show("Test"));
    }
}
