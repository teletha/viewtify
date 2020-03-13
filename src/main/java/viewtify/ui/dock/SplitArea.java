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
    protected void setFirstChild(ViewArea child) {
        super.setFirstChild(child);

        node.getItems().set(0, child.node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setSecondChild(ViewArea child) {
        super.setSecondChild(child);

        node.getItems().set(1, child.node);
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
