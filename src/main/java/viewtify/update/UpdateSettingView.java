/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
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

    private UILabel versionApp;

    private UIButton confirm;

    private UILabel versionOS;

    private UILabel versionJava;

    private UIButton reboot;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Confirm update on startup"), FormStyles.Column3, checkOnStartup);
                    form(en("Confirm update"), FormStyles.Column5, confirm);
                    form(en("Reboot application"), FormStyles.Column5, reboot);
                    form(en("Application"), FormStyles.Column5, versionApp);
                    form(en("Operating System"), FormStyles.Column5, versionOS);
                    form(en("Java Runtime"), FormStyles.Column5, versionJava);
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
        confirm.action(Update::apply);

        I.schedule(0, 6, TimeUnit.HOURS, false).to(() -> {
            if (Update.isAvailable(Viewtify.application().updateSite())) {
                confirm.text(en("Update to new version"));
            } else {
                confirm.text(en("This is latest version"));
            }
        });

        versionApp.text(app.launcher().getSimpleName() + " " + app.version());
        versionOS.text(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
        versionJava.text("Java " + Runtime.version());

        reboot.text(en("Reboot")).action(Viewtify.application()::reactivate);
    }
}