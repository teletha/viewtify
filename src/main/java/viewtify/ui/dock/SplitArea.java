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
import javafx.scene.control.Tab;

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
    protected void add(Tab view, int position) {
        if (firstChild == null) {
            setChild(0, new TabArea());
        }
        firstChild.add(view, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        if (index < node.getItems().size()) {
            node.getItems().set(index, child.node);
        } else {
            node.getItems().add(child.node);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setOrientation(Orientation orientation) {
        super.setOrientation(orientation);

        node.setOrientation(orientation);
    }

}
