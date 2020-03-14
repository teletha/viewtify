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
    void add(Tab view, int position) {
        switch (position) {
        case DockSystem.CENTER:
            if (firstChild != null) {
                firstChild.add(view, position);
            } else if (secondChild != null) {
                secondChild.add(view, position);
            }
            break;

        case DockSystem.TOP:
            if (orientation == Orientation.VERTICAL) {
                firstChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(target, this, Orientation.VERTICAL);
            }
            break;

        case DockSystem.BOTTOM:
            if (orientation == Orientation.VERTICAL) {
                secondChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(this, target, Orientation.VERTICAL);
            }
            break;

        case DockSystem.LEFT:
            if (orientation == Orientation.HORIZONTAL) {
                secondChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(target, this, Orientation.HORIZONTAL);
            }
            break;

        case DockSystem.RIGHT:
            if (orientation == Orientation.HORIZONTAL) {
                secondChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(this, target, Orientation.HORIZONTAL);
            }
            break;
        }
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
    protected void setOrientation(Orientation orientation) {
        super.setOrientation(orientation);

        node.setOrientation(orientation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void split(ViewArea first, ViewArea second, Orientation orientation) {
        ViewArea area = new SplitArea();
        parent.replace(this, area);
        area.setOrientation(orientation);
        area.setChild(0, first);
        area.setChild(1, second);
    }
}
