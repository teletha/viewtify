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

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

class SplitArea extends ViewArea<SplitPane> {

    /**
     * Create a new view area.
     */
    protected SplitArea() {
        super(new SplitPane());

        node.setOrientation(Orientation.VERTICAL);
        node.getItems().add(new Pane());
        node.getItems().add(new Pane());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        node.getItems().set(index, child.node);
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
    protected void setOrientation(Orientation orientation) {
        node.setOrientation(orientation);
    }
}
