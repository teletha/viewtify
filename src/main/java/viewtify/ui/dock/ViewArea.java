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

    /** The related area. */
    ViewArea firstChild;

    /** The realated area. */
    ViewArea secondChild;

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

        if (index == 0) {
            firstChild = child;
        } else {
            secondChild = child;
        }
    }

    /**
     * Get the firstChild property of this {@link ViewArea}.
     * 
     * @return The firstChild property.
     */
    final ViewArea getFirstChild() {
        return firstChild;
    }

    /**
     * Set the firstChild property of this {@link ViewArea}.
     * 
     * @param firstChild The firstChild value to set.
     */
    final void setFirstChild(ViewArea firstChild) {
        setChild(0, firstChild);
    }

    /**
     * Get the secondChild property of this {@link ViewArea}.
     * 
     * @return The secondChild property.
     */
    final ViewArea getSecondChild() {
        return secondChild;
    }

    /**
     * Set the secondChild property of this {@link ViewArea}.
     * 
     * @param secondChild The secondChild value to set.
     */
    final void setSecondChild(ViewArea secondChild) {
        setChild(1, secondChild);
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
     * Remove the given area as child from this area.
     * <p/>
     * In case of a underflow this area will also be removed.
     *
     * @param area The area that should be removed.
     */
    abstract void remove(ViewArea area);

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
    void replace(ViewArea oldArea, ViewArea newArea) {
        if (oldArea == firstChild) {
            setChild(0, newArea);
        } else if (oldArea == secondChild) {
            setChild(1, newArea);
        }
    }
}