/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.Button;

import viewtify.ui.helper.LabelHelper;

/**
 * @version 2018/09/09 11:59:42
 */
public class UIButton extends UserInterface<UIButton, Button> implements LabelHelper<UIButton, Button> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UIButton(View view) {
        super(new Button(), view);
    }
}
