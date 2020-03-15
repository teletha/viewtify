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
import java.util.Arrays;
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
import javafx.scene.control.skin.SplitPaneSkin;

import viewtify.Viewtify;

class SplitArea extends ViewArea<SplitPane> {

    /**
     * Create a new view area.
     */
    protected SplitArea() {
        super(new SplitPane());

        System.out.println("Create splitarea " + node);
        node.setSkin(new DumbSplitPaneSkin(node));
        node.getDividers().addListener((ListChangeListener<Divider>) c -> {
            while (c.next()) {
                for (Divider added : c.getAddedSubList()) {
                    Viewtify.observe(added.positionProperty()).debounce(1000, TimeUnit.MILLISECONDS).to(v -> {
                        DockSystem.layout.store();
                    });

                    added.positionProperty().addListener((o, p, n) -> {
                        new Error("Change from " + p + "  to " + n).printStackTrace();
                    });
                }
            }
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

    private static class DumbSplitPaneSkin extends SplitPaneSkin {

        public DumbSplitPaneSkin(SplitPane splitPane) {
            super(splitPane);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            double[] dividerPositions = getSkinnable().getDividerPositions();
            super.layoutChildren(x, y, w, h);
            getSkinnable().setDividerPositions(dividerPositions);
            System.out.println("Layout SplitArea " + Arrays.toString(dividerPositions) + "    " + Arrays
                    .toString(getSkinnable().getDividerPositions()));
        }
    }
}
