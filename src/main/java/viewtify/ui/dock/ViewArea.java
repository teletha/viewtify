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
        for (int i = 0; i < children.size(); i++) {
            setChild(i, children.get(i));
        }
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
    abstract void add(Tab view, int position);

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
     * Replace the {@param oldArea} with the {@param newArea}.
     *
     * @param oldArea The old area.
     * @param newArea The new area.
     */
    void replace(ViewArea oldArea, ViewArea newArea) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == oldArea) {
                setChild(i, newArea);
                return;
            }
        }
    }

    ViewArea findBy(String id) {
        for (ViewArea child : children) {
            ViewArea view = child.findBy(id);

            if (view != null) {
                return view;
            }
        }
        return null;
    }
}