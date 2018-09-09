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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.shape.Line;

import viewtify.Viewtify;

/**
 * @version 2018/01/05 2:30:03
 */
public class UILine extends UIShape<UILine, Line> {

    /**
     * 
     */
    public UILine() {
        super(new Line(), Viewtify.view());
    }

    public UILine startX(double value) {
        ui.setStartX(value);
        return this;
    }

    public UILine startY(double value) {
        ui.setStartY(value);
        return this;
    }

    public UILine endX(double value) {
        ui.setEndX(value);
        return this;
    }

    public UILine endX(ReadOnlyDoubleProperty value) {
        ui.endXProperty().unbind();
        ui.endXProperty().bind(value);
        return this;
    }

    public UILine endY(double value) {
        ui.setEndY(value);
        return this;
    }

    public UILine endY(ReadOnlyDoubleProperty value) {
        ui.endYProperty().unbind();
        ui.endYProperty().bind(value);
        return this;
    }
}
