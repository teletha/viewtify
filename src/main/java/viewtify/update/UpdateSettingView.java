/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.update;

import java.util.concurrent.TimeUnit;

import kiss.I;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.preference.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UICheckSwitch;
import viewtify.ui.UILabel;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class UpdateSettingView extends View {

    private UICheckSwitch checkOnStartup;

    private UICheckSwitch applyAuto;

    private UILabel versionApp;

    private UIButton confirm;

    private UILabel versionOS;

    private UILabel versionJava;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Confirm update on startup"), FormStyles.InputMin, checkOnStartup);
                    form(en("Apply update automatically"), FormStyles.InputMin, applyAuto);
                    form(en("Confirm update"), confirm);
                    form(en("Application"), versionApp);
                    form(en("Operating System"), versionOS);
                    form(en("Java Runtime"), versionJava);
                });
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable<String> title() {
        return en("Update");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        Viewtify app = Viewtify.application();
        UpdateSetting setting = Preferences.of(UpdateSetting.class);

        checkOnStartup.sync(setting.checkOnStartup);
        applyAuto.sync(setting.applyAuto);
        confirm.action(Update::apply);

        I.schedule(0, 6, TimeUnit.HOURS, false).to(() -> {
            if (Update.isAvailable(app.updateSite())) {
                confirm.text(en("Update to new version")).enable(true);
            } else {
                confirm.text(en("This is latest version")).disable(true);
            }
        });

        versionApp.text(app.launcher().getSimpleName() + " " + app.version());
        versionOS.text(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
        versionJava.text("Java " + Runtime.version());
    }
}
