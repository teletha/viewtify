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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import viewtify.ui.View;

/**
 * A ViewArea is a node within the area tree. It has two children which are self view areas.
 */
abstract class ViewArea<P extends Parent> {

    /** Managed views. */
    private static final Map<Parent, View> views = new ConcurrentHashMap();

    /** The actual root node. */
    protected final P node;

    /** The paretn area. */
    protected ViewArea parent;

    /** The related area. */
    protected ViewArea firstChild;

    /** The realated area. */
    protected ViewArea secondChild;

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

    /**
     * Register the event handler for drag&drop of views.
     *
     * @param node Register the event handlers on this node.
     */
    protected final void registerDragEvents(Node node) {
        node.setUserData(this);
        node.setOnDragOver(DockSystem::onDragOver);
        node.setOnDragExited(DockSystem::onDragExited);
        node.setOnDragDropped(DockSystem::onDragDropped);
    }

    /**
     * Set {@param child} as first child of this view area.
     * <p/>
     * It will also update the javafx scene graph and the childs parent value.
     *
     * @param child The new child.
     */
    protected void setFirstChild(ViewArea child) {
        // make relationship
        this.firstChild = child;
        child.parent = this;
    }

    /**
     * Set {@param child} as second child of this view area.
     * <p/>
     * It will also update the javafx scene graph and the childs parent value.
     *
     * @param child The new child.
     */
    protected void setSecondChild(ViewArea child) {
        // make relationship
        this.secondChild = child;
        child.parent = this;
    }

    /**
     * Add the view to this area at position.
     * <p/>
     * If position is {@link ViewPosition#CENTER} it will be added to that child that is defined as
     * editor area. Otherwise this area is split and the view will be positioned according the
     * position parameter.
     *
     * @param view The view to add.
     * @param position Add the view at this position.
     */
    protected void add(Tab view, ViewPosition position) {
        switch (position) {
        case CENTER:
            if (firstChild != null) {
                firstChild.add(view, position);
            } else if (secondChild != null) {
                secondChild.add(view, position);
            }
            break;

        case TOP:
            if (orientation == Orientation.VERTICAL) {
                firstChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, ViewPosition.CENTER);
                split(target, this, Orientation.VERTICAL);
            }
            break;

        case BOTTOM:
            if (orientation == Orientation.VERTICAL) {
                secondChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, ViewPosition.CENTER);
                split(this, target, Orientation.VERTICAL);
            }
            break;

        case LEFT:
            if (orientation == Orientation.HORIZONTAL) {
                secondChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, ViewPosition.CENTER);
                split(target, this, Orientation.HORIZONTAL);
            }
            break;

        case RIGHT:
            if (orientation == Orientation.HORIZONTAL) {
                secondChild.add(view, position);
            } else {
                ViewArea target = new TabArea();
                target.add(view, ViewPosition.CENTER);
                split(this, target, Orientation.HORIZONTAL);
            }
            break;
        }
        TabArea.of(view).node.requestLayout();
    }

    /**
     * Remove the given area as child from this area.
     * <p/>
     * In case of a underflow this area will also be removed.
     *
     * @param area The area that should be removed.
     */
    protected void remove(ViewArea area) {
        if (area == firstChild) {
            parent.replace(this, secondChild);
        } else if (area == secondChild) {
            parent.replace(this, firstChild);
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
        area.setFirstChild(first);
        area.setSecondChild(second);
    }

    /**
     * Replace the {@param oldArea} with the {@param newArea}.
     *
     * @param oldArea The old area.
     * @param newArea The new area.
     */
    private void replace(ViewArea oldArea, ViewArea newArea) {
        if (oldArea == firstChild) {
            setFirstChild(newArea);
        } else if (oldArea == secondChild) {
            setSecondChild(newArea);
        }
    }

    /**
     * Get the the root area where this view is registered.
     *
     * @return The root area of this view.
     */
    protected final RootArea getRootArea() {
        ViewArea current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return (RootArea) current;
    }

    /**
     * Set the orientation of the split area.
     *
     * @param orientation The orientation of splitting.
     */
    protected void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Is the drop gesture to this area with position center allowed?
     *
     * @return True if a drop to center is allowed.
     */
    protected boolean canDropToCenter() {
        return false;
    }
}