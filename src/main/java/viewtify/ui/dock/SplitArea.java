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
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        node.getItems().set(index, child.node);
    }

    /**
     * Get the pane orientation.
     * 
     * @return
     */
    final Orientation getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the split area.
     *
     * @param orientation The orientation of splitting.
     */
    void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        this.node.setOrientation(orientation);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    void add(Tab view, int position) {
        switch (position) {
        case DockSystem.CENTER:
            children.get(0).add(view, position);
            break;

        case DockSystem.TOP:
            if (orientation == Orientation.VERTICAL) {
                first().add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(target, this, Orientation.VERTICAL);
            }
            break;

        case DockSystem.BOTTOM:
            if (orientation == Orientation.VERTICAL) {
                last().add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(this, target, Orientation.VERTICAL);
            }
            break;

        case DockSystem.LEFT:
            if (orientation == Orientation.HORIZONTAL) {
                last().add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(target, this, Orientation.HORIZONTAL);
            }
            break;

        case DockSystem.RIGHT:
            if (orientation == Orientation.HORIZONTAL) {
                last().add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, DockSystem.CENTER);
                split(this, target, Orientation.HORIZONTAL);
            }
            break;
        }
    }

    /**
     * Split this area by {@param orientation}.
     * <p/>
     * Either the parameter {@param first} or {@param second} must be this area. Otherwise a
     * {@link IllegalArgumentException} is thrown.
     *
     * @param first The first element.
     * @param second The second element.
     * @param orientation The split orientation.
     * @throws IllegalArgumentException In case of both params {@param first} and {@param second}
     *             are this or none of them.
     */
    void split(ViewArea first, ViewArea second, Orientation orientation) {
        if (!(first == this ^ second == this)) {
            throw new IllegalArgumentException("Either first or second area must be this.");
        }

        SplitArea area = new SplitArea();
        parent.replace(this, area);
        area.setOrientation(orientation);
        area.setChild(0, first);
        area.setChild(1, second);
    }

    ViewArea first() {
        return children.get(0);
    }

    ViewArea last() {
        return children.get(children.size() - 1);
    }
}
