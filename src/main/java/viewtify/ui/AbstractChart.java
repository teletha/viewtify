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

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import kiss.I;
import kiss.Signal;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;

public abstract class AbstractChart<Self extends AbstractChart<Self, C, D>, C extends Chart, D> extends UserInterface<Self, C>
        implements UserActionHelper<Self> {

    /** The actual tooltip. */
    private final Tooltip tooltip = new Tooltip();

    /** The tooltip pane. */
    protected final VBox root = new VBox();

    /** The tooltip showing state. */
    private boolean shown;

    /** The number of tooltip labels that can be displayed. */
    private int tooltipLabels = Integer.MAX_VALUE;

    /**
     * @param view
     */
    protected AbstractChart(C chart, View view) {
        super(chart, view);

        tooltip.setGraphic(root);

        when(User.MouseExit, this::hideTooltip);
        when(User.MouseMove, e -> {
            if (!showTooltip(e)) {
                hideTooltip(e);
            }
        });
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

    /**
     * Limit the number of tooltip labels.
     * 
     * @param size
     * @return
     */
    public Self limitLabel(int size) {
        this.tooltipLabels = size;

        for (int i = 0; i < root.getChildren().size(); i++) {
            Node node = root.getChildren().get(i);
            node.setManaged(i < size);
            node.setVisible(i < size);
        }
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

    /**
     * Show tooltip.
     * 
     * @param e
     * @return
     */
    protected boolean showTooltip(MouseEvent e) {
        Bounds outer = ui.localToScreen(ui.getBoundsInLocal());
        double x = outer.getMinX() + e.getX() + 20;
        double y = outer.getMinY() + e.getY() - 15;

        if (shown) {
            tooltip.setX(x);
            tooltip.setY(y);
        } else {
            shown = true;
            tooltip.show(ui, x, y);
        }
        return true;
    }

    /**
     * Hide tooltip.
     * 
     * @param e
     */
    protected void hideTooltip(MouseEvent e) {
        if (shown) {
            shown = false;
            tooltip.hide();
        }
    }

    /**
     * Create tooltip label.
     * 
     * @param colorId
     * @return
     */
    protected Label createTooltipLabel(int colorId) {
        Circle mark = new Circle(3);
        mark.setFill(Color.TRANSPARENT);
        mark.setStrokeWidth(2);
        mark.getStyleClass().addAll("default-color" + colorId, "chart-series-line");

        Label label = new Label();
        label.setGraphic(mark);
        label.setGraphicTextGap(8);
        label.setPadding(new Insets(5, 0, 2, 0));
        label.setVisible(root.getChildren().size() < tooltipLabels);
        label.setManaged(root.getChildren().size() < tooltipLabels);

        return label;
    }
}