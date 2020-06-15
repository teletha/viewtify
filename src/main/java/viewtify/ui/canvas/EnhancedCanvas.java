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

import java.util.Objects;
import java.util.function.Consumer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import viewtify.util.FXUtils;

public class EnhancedCanvas extends Canvas {

    /**
     * Bind the canvas size to the parent region.
     * 
     * @param parent
     * @return
     */
    public EnhancedCanvas fitOn(Region parent) {
        widthProperty().bind(parent.widthProperty());
        heightProperty().bind(parent.heightProperty());
        return this;
    }

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

    /**
     * Clear all drawing figure on this canvas.
     * 
     * @return Chainable API.
     */
    public EnhancedCanvas clear() {
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        return this;
    }

    /**
     * Configure stroke style.
     * 
     * @param color
     * @return Chainable API.
     */
    public EnhancedCanvas configureStroke(stylist.value.Color color) {
        return configureStroke(FXUtils.color(color));
    }

    /**
     * Configure stroke style.
     * 
     * @param color
     * @return Chainable API.
     */
    public EnhancedCanvas configureStroke(Color color) {
        getGraphicsContext2D().setStroke(color);
        return this;
    }

    /**
     * Stroke text on the specified position.
     * 
     * @param text A text to draw.
     * @param x X position.
     * @param y Y position.
     * @return Chainable API.
     */
    public EnhancedCanvas strokeText(Object text, double x, double y) {
        getGraphicsContext2D().strokeText(Objects.toString(text), x, y);
        return this;
    }

    /**
     * Stroke text on the specified position.
     * 
     * @param text A text to draw.
     * @param x X position.
     * @param y Y position.
     * @param width A text width.
     * @return Chainable API.
     */
    public EnhancedCanvas strokeText(Object text, double x, double y, double width) {
        getGraphicsContext2D().strokeText(Objects.toString(text), x, y, width);
        return this;
    }

    /**
     * Fill text on the specified position.
     * 
     * @param text A text to draw.
     * @param x X position.
     * @param y Y position.
     * @return Chainable API.
     */
    public EnhancedCanvas fillText(Object text, double x, double y) {
        getGraphicsContext2D().fillText(Objects.toString(text), x, y);
        return this;
    }

    /**
     * Fill text on the specified position.
     * 
     * @param text A text to draw.
     * @param x X position.
     * @param y Y position.
     * @param width A text width.
     * @return Chainable API.
     */
    public EnhancedCanvas fillText(Object text, double x, double y, double width) {
        getGraphicsContext2D().fillText(Objects.toString(text), x, y, width);
        return this;
    }
}
