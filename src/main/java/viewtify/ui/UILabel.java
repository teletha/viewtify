/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.control.Label;

import viewtify.View;
import viewtify.ui.helper.LabelHelper;

/**
 * @version 2018/01/23 13:49:29
 */
public class UILabel extends UserInterface<UILabel, Label> implements LabelHelper<UILabel, Label> {

    /**
     * @param ui
     * @param view
     */
    public UILabel(Label ui, View view) {
        super(ui, view);
    }
}
