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

import javafx.beans.property.Property;
import javafx.scene.control.ProgressBar;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.ValueHelper;

public class UIProgressBar extends UserInterface<UIProgressBar, ProgressBar>
        implements ContextMenuHelper<UIProgressBar>, ValueHelper<UIProgressBar, Double> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIProgressBar(View view) {
        super(new ProgressBar(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Double> valueProperty() {
        return ui.progressProperty().asObject();
    }
}