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

    /** The area orientation. */
    private Orientation orientation;

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
     * Get the orientation property of this {@link SplitArea}.
     * 
     * @return The orientation property.
     */
    @Override
    final Orientation getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation property of this {@link SplitArea}.
     * 
     * @param orientation The orientation value to set.
     */
    final void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        this.node.setOrientation(orientation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void remove(ViewArea area) {
        if (area == firstChild) {
            parent.replace(this, secondChild);
        } else if (area == secondChild) {
            parent.replace(this, firstChild);
        }
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
    void split(ViewArea first, ViewArea second, Orientation orientation) {
        SplitArea area = new SplitArea();
        parent.replace(this, area);
        area.setOrientation(orientation);
        area.setChild(0, first);
        area.setChild(1, second);
    }
}
