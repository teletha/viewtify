/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.Label;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;

public class UILabel extends UserInterface<UILabel, Label> implements LabelHelper<UILabel>, ContextMenuHelper<UILabel> {

    /**
     * Build {@link Label}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UILabel(View view) {
        super(new Label(), view);
    }
}