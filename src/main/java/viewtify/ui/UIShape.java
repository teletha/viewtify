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

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import viewtify.View;

/**
 * @version 2018/01/05 2:31:23
 */
public class UIShape<Self extends UIShape, S extends Shape> extends UserInterface<Self, S> {

    /**
     * @param ui
     * @param view
     */
    public UIShape(S ui, View view) {
        super(ui, view);
    }

    public Self stroke(Paint color) {
        ui.setStroke(color);
        return (Self) this;
    }

    public Self strokeWidth(double width) {
        ui.setStrokeWidth(width);
        return (Self) this;
    }
}
