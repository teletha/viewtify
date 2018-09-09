/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.Label;

import viewtify.View;
import viewtify.ui.helper.LabelHelper;

/**
 * @version 2018/09/09 11:47:48
 */
public class UILabel extends UserInterface<UILabel, Label> implements LabelHelper<UILabel, Label> {

    /**
     * Build {@link Label}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    UILabel(View view) {
        super(new Label(), view);
    }
}
