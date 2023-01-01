/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.layout.HBox;

import viewtify.ui.helper.ContainerHelper;
import viewtify.ui.helper.ContextMenuHelper;

public class UIHBox extends UserInterface<UIHBox, HBox> implements ContextMenuHelper<UIHBox>, ContainerHelper<UIHBox, HBox> {

    /**
     * @param view
     */
    public UIHBox(View view) {
        super(new HBox(), view);
    }
}