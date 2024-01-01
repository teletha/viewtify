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

import javafx.scene.layout.VBox;
import viewtify.ui.helper.ContainerHelper;
import viewtify.ui.helper.ContextMenuHelper;

public class UIVBox extends UserInterface<UIVBox, VBox> implements ContextMenuHelper<UIVBox>, ContainerHelper<UIVBox, VBox> {

    /**
     * @param view
     */
    public UIVBox(View view) {
        super(new VBox(), view);
    }
}