/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import kiss.I;
import kiss.Signal;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;

public abstract class AbstractChart<Self extends AbstractChart<Self, C, D>, C extends Chart, D> extends UserInterface<Self, C>
        implements UserActionHelper<Self> {

    private VBox root;

    private Tooltip tooltip;

    private boolean popuped;

    private int legendLimit;

    /**
     * @param view
     */
    protected AbstractChart(C chart, View view) {
        super(chart, view);
    }

    /**
     * Set title.
     * 
     * @param title
     * @return
     */
    public Self title(String title) {
        ui.setTitle(title);
        return (Self) this;
    }

    /**
     * Set title location.
     * 
     * @param side
     * @return
     */
    public Self title(Side side) {
        ui.setTitleSide(side);
        return (Self) this;
    }

    /**
     * Set legends.
     * 
     * @param enable
     * @return
     */
    public Self legend(boolean enable) {
        ui.setLegendVisible(enable);
        return (Self) this;
    }

    /**
     * Set legends location.
     * 
     * @param side
     * @return
     */
    public Self legend(Side side) {
        ui.setLegendSide(side);
        return (Self) this;
    }

    public Self limitLegend(int size) {
        this.legendLimit = size;
        return (Self) this;
    }

    /**
     * Provide new data.
     * 
     * @param data
     * @return
     */
    public final Self data(D data) {
        return data("", data);
    }

    /**
     * Provide new data.
     * 
     * @param data
     * @return
     */
    public final Self data(Iterable<D> data) {
        return data("", data);
    }

    /**
     * Provide new data.
     * 
     * @param data
     * @return
     */
    public final Self data(Signal<D> data) {
        return data("", data);
    }

    /**
     * Provide new data.
     * 
     * @param data
     * @return
     */
    public final Self data(String name, D data) {
        return data(name, I.signal(data));
    }

    /**
     * Provide new data.
     * 
     * @param data
     * @return
     */
    public final Self data(String name, Iterable<D> data) {
        return data(name, I.signal(data));
    }

    /**
     * Provide new data.
     * 
     * @param data
     * @return
     */
    public abstract Self data(String name, Signal<D> data);

    public Self popup() {
        root = new VBox();
        tooltip = new Tooltip();
        tooltip.setGraphic(root);

        when(User.MouseMove, e -> {
            if (!showTooltip(e)) {
                hideTooltip(e);
            }
        });
        when(User.MouseExit, this::hideTooltip);
        return (Self) this;
    }

    /**
     * Get the tooltip pane.
     * 
     * @return
     */
    protected ObservableList<Node> tooltip() {
        return root.getChildren();
    }

    protected boolean showTooltip(MouseEvent e) {
        Bounds outer = ui.localToScreen(ui.getBoundsInLocal());
        double x = outer.getMinX() + e.getX() + 20;
        double y = outer.getMinY() + e.getY() - 15;

        if (popuped) {
            tooltip.setX(x);
            tooltip.setY(y);
        } else {
            popuped = true;
            tooltip.show(ui, x, y);
        }
        return true;
    }

    protected void hideTooltip(MouseEvent e) {
        if (popuped) {
            popuped = false;
            tooltip.hide();
        }
    }
}
