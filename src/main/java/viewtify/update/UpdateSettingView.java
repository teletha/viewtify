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

import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UIButton;
import viewtify.ui.UILabel;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class UpdateSettingView extends View {

    private UILabel version;

    private UIButton confirm;

    private UILabel osVersion;

    private UILabel javaVersion;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Current version"), version);
                    form(en("Confirm update"), confirm);
                    form(en("OS Specification"), osVersion);
                    form(en("Java Specification"), javaVersion);
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

        version.text(app.version());
        confirm.text(en("Confirm")).action(Update::apply);

        osVersion.text(System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
        javaVersion.text("Java " + Runtime.version());
    }
}
