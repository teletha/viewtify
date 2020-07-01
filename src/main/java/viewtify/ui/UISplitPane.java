/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

import viewtify.ui.helper.ContextMenuHelper;

public class UISplitPane extends UserInterface<UISplitPane, SplitPane> implements ContextMenuHelper<UISplitPane> {

    /**
     * @param ui
     * @param view
     */
    public UISplitPane(View view) {
        super(new SplitPane(), view);

        ui.setOrientation(Orientation.VERTICAL);
    }
}