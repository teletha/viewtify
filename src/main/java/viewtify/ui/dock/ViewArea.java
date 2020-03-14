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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * A ViewArea is a node within the area tree. It has two children which are self view areas.
 */
abstract class ViewArea<P extends Parent> {

    /** The actual root node. */
    protected final P node;

    /** The paretn area. */
    protected ViewArea parent;

    List<ViewArea> children = new ArrayList();

    /** The area orientation. */
    private Orientation orientation;

    /**
     * Specify root node.
     * 
     * @param node The root node of this area.
     */
    protected ViewArea(P node) {
        this.node = Objects.requireNonNull(node);
    }

    protected void setChild(int index, ViewArea child) {
        child.parent = this;

        if (index < children.size()) {
            children.set(index, child);
        } else {
            children.add(child);
        }
    }

    /**
     * Get the children property of this {@link ViewArea}.
     * 
     * @return The children property.
     */
    final List<ViewArea> getChildren() {
        return children;
    }

    /**
     * Set the children property of this {@link ViewArea}.
     * 
     * @param children The children value to set.
     */
    final void setChildren(List<ViewArea> children) {
        this.children = children;
    }

    private ViewArea first() {
        return children.get(0);
    }

    private ViewArea last() {
        return children.get(children.size() - 1);
    }

    /**
     * Add the view to this area at position.
     * <p/>
     * If position is {@link DockSystem#CENTER} it will be added to that child that is defined as
     * editor area. Otherwise this area is split and the view will be positioned according the
     * position parameter.
     *
     * @param view The view to add.
     * @param position Add the view at this position.
     */
    protected void add(Tab view, int position) {
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
     * Remove the given area as child from this area.
     * <p/>
     * In case of a underflow this area will also be removed.
     *
     * @param area The area that should be removed.
     */
    protected void remove(ViewArea area) {
        children.remove(area);

        if (children.size() == 1) {
            parent.replace(this, children.remove(0));
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
    private void split(ViewArea first, ViewArea second, Orientation orientation) {
        if (!(first == this ^ second == this)) {
            throw new IllegalArgumentException("Either first or second area must be this.");
        }

        ViewArea area = new SplitArea();
        parent.replace(this, area);
        area.setOrientation(orientation);
        area.setChild(0, first);
        area.setChild(1, second);
    }

    /**
     * Replace the {@param oldArea} with the {@param newArea}.
     *
     * @param oldArea The old area.
     * @param newArea The new area.
     */
    private void replace(ViewArea oldArea, ViewArea newArea) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == oldArea) {
                setChild(i, newArea);
                return;
            }
        }
    }

    /**
     * Get the pane orientation.
     * 
     * @return
     */
    protected final Orientation getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the split area.
     *
     * @param orientation The orientation of splitting.
     */
    protected void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}