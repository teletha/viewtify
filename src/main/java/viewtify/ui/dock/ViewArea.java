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

import kiss.Variable;
import viewtify.ui.UITab;
import viewtify.ui.UserInterface;

/**
 * A ViewArea is a node within the area tree. It has two children which are self view areas.
 */
abstract class ViewArea<P extends UserInterface<P, ? extends Parent>> {

    /** The area position. */
    private int position;

    /** The actual root node. */
    protected final P node;

    /** The paretn area. */
    protected ViewArea parent;

    /** The related area. */
    List<ViewArea> children = new ArrayList();

    /**
     * Specify root node.
     * 
     * @param node The root node of this area.
     */
    protected ViewArea(P node) {
        this.node = Objects.requireNonNull(node);
    }

    /**
     * Get the position property of this {@link ViewArea}.
     * 
     * @return The position property.
     */
    final int getPosition() {
        return position;
    }

    /**
     * Set the position property of this {@link ViewArea}.
     * 
     * @param position The position value to set.
     */
    final void setPosition(int position) {
        this.position = position;
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
    @SuppressWarnings("unused")
    private final List<ViewArea> getChildren() {
        return children;
    }

    /**
     * Set the children property of this {@link ViewArea}.
     * 
     * @param children The children value to set.
     */
    @SuppressWarnings("unused")
    private final void setChildren(List<ViewArea> children) {
        for (int i = 0; i < children.size(); i++) {
            setChild(i, children.get(i));
        }
    }

    /**
     * Add the view to this area at position.
     * <p/>
     * If position is {@link DockSystem#PositionCenter} it will be added to that child that is
     * defined as editor area. Otherwise this area is split and the view will be positioned
     * according the position parameter.
     *
     * @param view The view to add.
     * @param position Add the view at this position.
     */
    public TabArea add(UITab view, int position) {
        switch (position) {
        case DockSystem.PositionCenter:
            return children.get(0).add(view, position);

        case DockSystem.PositionTop:
            if (getOrientation() == Orientation.VERTICAL) {
                return children.get(0).add(view, position);
            } else {
                ViewArea target = new TabArea();
                split(target, this, Orientation.VERTICAL);
                return target.add(view, DockSystem.PositionCenter);
            }

        case DockSystem.PositionBottom:
            if (getOrientation() == Orientation.VERTICAL) {
                return children.get(children.size() - 1).add(view, position);
            } else {
                ViewArea target = new TabArea();
                split(this, target, Orientation.VERTICAL);
                return target.add(view, DockSystem.PositionCenter);
            }

        case DockSystem.PositionLeft:
            if (getOrientation() == Orientation.HORIZONTAL) {
                return children.get(children.size() - 1).add(view, position);
            } else {
                ViewArea target = new TabArea();
                split(target, this, Orientation.HORIZONTAL);
                return target.add(view, DockSystem.PositionCenter);
            }

        default:
            if (getOrientation() == Orientation.HORIZONTAL) {
                return children.get(children.size() - 1).add(view, position);
            } else {
                ViewArea target = new TabArea();
                split(this, target, Orientation.HORIZONTAL);
                return target.add(view, DockSystem.PositionCenter);
            }
        }
    }

    /**
     * Remove the given area as child from this area.
     * <p/>
     * In case of a underflow this area will also be removed.
     *
     * @param area The area that should be removed.
     */
    public void remove(ViewArea area) {
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
        SplitArea area = new SplitArea();
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
                oldArea.parent = null;
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
    protected Orientation getOrientation() {
        return null;
    }

    /**
     * Find {@link ViewArea} from descendent nodes.
     * 
     * @param id
     * @return
     */
    protected Variable<ViewArea> findAreaBy(String id) {
        for (ViewArea child : children) {
            Variable<ViewArea> area = child.findAreaBy(id);

            if (area.isPresent()) {
                return area;
            }
        }
        return Variable.empty();
    }
}