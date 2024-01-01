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

import javafx.scene.layout.Pane;

import viewtify.ui.helper.ContainerHelper;

public class UIPane extends UserInterface<UIPane, Pane> implements ContainerHelper<UIPane, Pane> {

    /**
     * @param view
     */
    public UIPane(View view) {
        super(new Pane(), view);
    }

    /**
     * @param pane
     * @param view
     */
    public UIPane(Pane pane, View view) {
        super(pane, view);
    }
}