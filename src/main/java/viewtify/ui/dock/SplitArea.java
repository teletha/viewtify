/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.dock;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane.Divider;

import viewtify.ui.UISplitPane;
import viewtify.ui.helper.User;

class SplitArea extends ViewArea<UISplitPane> {

    /** The latest divider's positions. */
    private double[] snapshot = new double[0];

    /**
     * Create a new view area.
     */
    protected SplitArea() {
        super(new UISplitPane(null));

        // The SplitPane implements content replacement in two stages: removal and addition.
        // For this reason, when removing content, divider is also removed, and the divider's
        // position information will be lost.
        // Therefore, each time the divider's position information is changed, it is stored in
        // the cache and restored every time the dividier increases or decreases.
        node.ui.getDividers().addListener((ListChangeListener<Divider>) c -> {
            while (c.next()) {
                for (Divider added : c.getAddedSubList()) {
                    added.positionProperty().addListener((o, p, n) -> {
                        DockSystem.requestSavingLayout();
                        snapshot = node.ui.getDividerPositions();
                    });
                }
            }
            node.ui.setDividerPositions(snapshot);
        });

        node.when(User.DragOver, e -> DockSystem.onSplitterDragOver(e, this));
        node.when(User.DragDrop, e -> DockSystem.onSplitterDragDropped(e, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        ObservableList<Node> items = node.ui.getItems();
        if (index < items.size()) {
            items.set(index, child.node.ui);
        } else {
            items.add(child.node.ui);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Orientation getOrientation() {
        return node.ui.getOrientation();
    }

    /**
     * @param orientation
     */
    void setOrientation(Orientation orientation) {
        node.ui.setOrientation(orientation);
    }

    /**
     * Get the dividers property of this {@link SplitArea}.
     * 
     * @return The dividers property.
     */
    @SuppressWarnings("unused")
    private final List<Double> getDividers() {
        return DoubleStream.of(node.ui.getDividerPositions()).boxed().collect(Collectors.toList());
    }

    /**
     * Set the dividers property of this {@link SplitArea}.
     * 
     * @param dividers The dividers value to set.
     */
    private final void setDividers(List<Double> dividers) {
        // During Stage initialization, window size changes several times until layout is completed.
        // Every change modifies divider positions. If we want to control divider positions, they
        // have to be set after Stage is fully initialized:
        Platform.runLater(() -> {
            double[] values = new double[dividers.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = dividers.get(i);
            }
            node.ui.setDividerPositions(values);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setViewportRatio(double ratio) {
        setDividers(List.of(ratio));
    }
}