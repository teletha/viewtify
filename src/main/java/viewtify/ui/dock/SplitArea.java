/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

class SplitArea extends ViewArea<SplitPane> {

    /**
     * Create a new view area.
     */
    protected SplitArea() {
        super(new SplitPane());

        node.setOrientation(Orientation.VERTICAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        ObservableList<Node> items = node.getItems();
        if (index < items.size()) {
            items.set(index, child.node);
        } else {
            items.add(child.node);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Orientation getOrientation() {
        return node.getOrientation();
    }

    /**
     * {@inheritDoc}
     */
    void setOrientation(Orientation orientation) {
        node.setOrientation(orientation);
    }

    /**
     * Get the dividers property of this {@link SplitArea}.
     * 
     * @return The dividers property.
     */
    @SuppressWarnings("unused")
    private final List<Double> getDividers() {
        return DoubleStream.of(node.getDividerPositions()).boxed().collect(Collectors.toList());
    }

    /**
     * Set the dividers property of this {@link SplitArea}.
     * 
     * @param dividers The dividers value to set.
     */
    @SuppressWarnings("unused")
    private final void setDividers(List<Double> dividers) {
        double[] values = new double[dividers.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = dividers.get(i);
        }
        node.setDividerPositions(values);
    }
}
