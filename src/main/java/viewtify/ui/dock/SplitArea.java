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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;

import viewtify.Viewtify;

class SplitArea extends ViewArea<SplitPane> {

    /** The latest divider's positions. */
    private double[] snapshot = new double[0];

    /**
     * Create a new view area.
     */
    protected SplitArea() {
        super(new SplitPane());

        // The SplitPane implements content replacement in two stages: removal and addition.
        // For this reason, when removing content, divider is also removed, and the divider's
        // position information will be lost.
        // Therefore, each time the divider's position information is changed, it is stored in
        // the cache and restored every time the dividier increases or decreases.
        node.getDividers().addListener((ListChangeListener<Divider>) c -> {
            while (c.next()) {
                for (Divider added : c.getAddedSubList()) {
                    Viewtify.observe(added.positionProperty()).debounce(1000, TimeUnit.MILLISECONDS).to(v -> {
                        DockSystem.saveLayout();
                        snapshot = node.getDividerPositions();
                    });
                }
            }
            node.setDividerPositions(snapshot);
        });
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
    private final List<BigDecimal> getDividers() {
        return DoubleStream.of(node.getDividerPositions())
                .mapToObj(v -> new BigDecimal(v).setScale(3, RoundingMode.HALF_DOWN))
                .collect(Collectors.toList());
    }

    /**
     * Set the dividers property of this {@link SplitArea}.
     * 
     * @param dividers The dividers value to set.
     */
    @SuppressWarnings("unused")
    private final void setDividers(List<BigDecimal> dividers) {
        // During Stage initialization, window size changes several times until layout is completed.
        // Every change modifies divider positions. If we want to control divider positions, they
        // have to be set after Stage is fully initialized:
        Platform.runLater(() -> {
            double[] values = new double[dividers.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = dividers.get(i).doubleValue();
            }
            node.setDividerPositions(values);
        });
    }
}
