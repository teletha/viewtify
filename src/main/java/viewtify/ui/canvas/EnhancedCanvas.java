/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.canvas;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.VisibleHelper;
import viewtify.util.FXUtils;

public class EnhancedCanvas extends Canvas implements StyleHelper<EnhancedCanvas, EnhancedCanvas>, VisibleHelper<EnhancedCanvas> {

    /**
     * {@inheritDoc}
     */
    @Override
    public EnhancedCanvas ui() {
        return this;
    }

    /**
     * Bind the canvas size to the parent region.
     * 
     * @param parent
     * @return
     */
    public EnhancedCanvas bindSizeTo(Region parent) {
        heightProperty().bind(parent.heightProperty());
        widthProperty().bind(parent.widthProperty());
        return this;
    }

    /**
     * Bind the canvas size to the parent region.
     * 
     * @param height
     * @return
     */
    public EnhancedCanvas bindSizeTo(double width, Region height) {
        heightProperty().bind(height.heightProperty());
        setWidth(width);
        return this;
    }

    /**
     * Bind the canvas size to the parent region.
     * 
     * @param width
     * @return
     */
    public EnhancedCanvas bindSizeTo(Region width, double height) {
        setHeight(height);
        widthProperty().bind(width.widthProperty());
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
     * Set the width and height of this canvas.
     * 
     * @param width A width to set.
     * @return Chainable API.
     */
    public EnhancedCanvas width(double width) {
        if (widthProperty().isBound()) {
            widthProperty().unbind();
        }
        setWidth(width);
        return this;
    }

    /**
     * Set the width and height of this canvas.
     * 
     * @param height A height to set.
     * @return Chainable API.
     */
    public EnhancedCanvas height(double height) {
        if (heightProperty().isBound()) {
            heightProperty().unbind();
        }
        setHeight(height);
        return this;
    }

    /**
     * Set the width and height of this canvas.
     * 
     * @param width A width to set.
     * @param height A height to set.
     * @return Chainable API.
     */
    public EnhancedCanvas size(double width, double height) {
        width(width);
        height(height);
        return this;
    }

    /**
     * Set the width and height of this canvas.
     * 
     * @param width A width to set.
     * @return Chainable API.
     */
    public EnhancedCanvas lineWidth(double width) {
        getGraphicsContext2D().setLineWidth(width);
        return this;
    }

    /**
     * Set the text base line of this canvas.
     * 
     * @param baseLine A baseLine to set.
     * @return Chainable API.
     */
    public EnhancedCanvas textBaseLine(VPos baseLine) {
        getGraphicsContext2D().setTextBaseline(baseLine);
        return this;
    }

    /**
     * Configure fill color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    public EnhancedCanvas fillColor(stylist.value.Color color) {
        return fillColor(FXUtils.color(color));
    }

    /**
     * Configure fill color.
     * 
     * @param red A red element.
     * @param green A green element.
     * @param blue A blue element.
     * @return Chainable API.
     */
    public EnhancedCanvas fillColor(int red, int green, int blue) {
        return fillColor(Color.rgb(red, green, blue));
    }

    /**
     * Configure fill color.
     * 
     * @param red A red element.
     * @param green A green element.
     * @param blue A blue element.
     * @return Chainable API.
     */
    public EnhancedCanvas fillColor(int red, int green, int blue, double alpha) {
        return fillColor(Color.rgb(red, green, blue, alpha));
    }

    /**
     * Configure fill color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    public EnhancedCanvas fillColor(Color color) {
        getGraphicsContext2D().setFill(color);
        return this;
    }

    /**
     * Configure stroke color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    public EnhancedCanvas strokeColor(stylist.value.Color color) {
        return strokeColor(FXUtils.color(color));
    }

    /**
     * Configure stroke color.
     * 
     * @param red A red element.
     * @param green A green element.
     * @param blue A blue element.
     * @return Chainable API.
     */
    public EnhancedCanvas strokeColor(int red, int green, int blue) {
        return strokeColor(Color.rgb(red, green, blue));
    }

    /**
     * Configure stroke color.
     * 
     * @param red A red element.
     * @param green A green element.
     * @param blue A blue element.
     * @return Chainable API.
     */
    public EnhancedCanvas strokeColor(int red, int green, int blue, double alpha) {
        return strokeColor(Color.rgb(red, green, blue, alpha));
    }

    /**
     * Configure stroke color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    public EnhancedCanvas strokeColor(Color color) {
        getGraphicsContext2D().setStroke(color);
        return this;
    }

    /**
     * Get the current font size.
     * 
     * @return
     */
    public double fontSize() {
        return getGraphicsContext2D().getFont().getSize();
    }

    /**
     * Configure font size..
     * 
     * @param size A font size to set.
     * @return Chainable API.
     */
    public EnhancedCanvas font(double size) {
        return font(size, null);
    }

    /**
     * Configure font size..
     * 
     * @param size A font size to set.
     * @return Chainable API.
     */
    public EnhancedCanvas font(double size, FontWeight weight) {
        getGraphicsContext2D().setFont(Font.font(null, weight, size));
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

    /**
     * Fill text on the specified position.
     * 
     * @param x X position.
     * @param y Y position.
     * @param width A width.
     * @param height A height.
     * @return Chainable API.
     */
    public EnhancedCanvas fillRect(double x, double y, double width, double height) {
        getGraphicsContext2D().fillRect(x, y, width, height);
        return this;
    }

    /**
     * Draw your art when the specified timing.
     * 
     * @param <T>
     * @param timing
     * @param drawing
     * @return Chainable API.
     */
    public <T> EnhancedCanvas drawWhen(Signal<T> timing, BiConsumer<T, EnhancedCanvas> drawing) {
        if (timing != null && drawing != null) {
            timing.on(Viewtify.UIThread).to(context -> drawing.accept(context, this));
        }
        return this;
    }
}