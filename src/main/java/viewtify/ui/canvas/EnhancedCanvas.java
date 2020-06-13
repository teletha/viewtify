/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.canvas;

import java.util.function.Consumer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class EnhancedCanvas extends Canvas {

    /**
     * Configure graphic context.
     * 
     * @param context
     * @return
     */
    public EnhancedCanvas context(Consumer<GraphicsContext> context) {
        if (context != null) {
            context.accept(getGraphicsContext2D());
        }
        return this;
    }
}
