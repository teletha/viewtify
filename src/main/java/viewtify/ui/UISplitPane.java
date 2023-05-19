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

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.UserActionHelper;

public class UISplitPane extends UserInterface<UISplitPane, SplitPane>
        implements ContextMenuHelper<UISplitPane>, DisableHelper<UISplitPane>, UserActionHelper<UISplitPane> {

    /**
     * @param view
     */
    public UISplitPane(View view) {
        super(new SplitPane(), view);

        ui.setOrientation(Orientation.VERTICAL);
    }

}