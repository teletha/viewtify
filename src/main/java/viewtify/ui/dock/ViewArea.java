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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

/**
 * A ViewArea is a node within the area tree. It has two children which are self view areas.
 */
public class ViewArea {

    private final SplitPane outerPane;

    protected final DNDManager dndManager;

    private ViewArea parent;

    private ViewArea firstChild;

    private ViewArea secondChild;

    private Orientation orientation;

    /** Did this area contains the editor pane? */
    private boolean editor;

    /**
     * Create a new view area and register the given area as parent.
     *
     * @param parent The parent area.
     * @param dndManager The drag&drop manager to handle moving the contained views.
     */
    protected ViewArea(ViewArea parent, DNDManager dndManager) {
        this(dndManager);
        this.parent = parent;
    }

    /**
     * Create a new view area.
     *
     * @param dndManager The drag&drop manager to handle moving the contained views.
     */
    protected ViewArea(DNDManager dndManager) {
        outerPane = new SplitPane();
        outerPane.setOrientation(Orientation.VERTICAL);
        outerPane.getItems().add(new Pane());
        outerPane.getItems().add(new Pane());
        this.dndManager = dndManager;
        registerDragEvents(outerPane);
    }

    /**
     * Register the event handler for drag&drop of views.
     *
     * @param node Register the event handlers on this node.
     */
    protected final void registerDragEvents(Node node) {
        node.setUserData(this);
        node.setOnDragOver(dndManager::onDragOver);
        node.setOnDragExited(dndManager::onDragExited);
        node.setOnDragDropped(dndManager::onDragDropped);
    }

    /**
     * Get the javafx scene graph node which represents this area.
     *
     * @return The scene graph node.
     */
    protected Parent getNode() {
        return outerPane;
    }

    /**
     * Get the parent area.
     * 
     * @return
     */
    protected final ViewArea getParent() {
        return parent;
    }

    /**
     * Get the first area.
     * 
     * @return
     */
    protected final ViewArea getFirstChild() {
        return firstChild;
    }

    /**
     * Set {@param child} as first child of this view area.
     * <p/>
     * It will also update the javafx scene graph and the childs parent value.
     *
     * @param child The new child.
     */
    protected void setFirstChild(ViewArea child) {
        this.firstChild = child;
        outerPane.getItems().set(0, child.getNode());
        child.parent = this;
    }

    /**
     * Get the second area.
     * 
     * @return
     */
    protected final ViewArea getSecondChild() {
        return secondChild;
    }

    /**
     * Set {@param child} as second child of this view area.
     * <p/>
     * It will also update the javafx scene graph and the childs parent value.
     *
     * @param child The new child.
     */
    protected void setSecondChild(ViewArea child) {
        this.secondChild = child;
        outerPane.getItems().set(1, child.getNode());
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
    protected void add(ViewStatus view, ViewPosition position) {
        switch (position) {
        case CENTER:
            getEditorArea().add(view, position);
            break;
        case TOP:
            if (orientation == Orientation.VERTICAL) {
                getFirstChild().add(view, position);
            } else {
                ViewArea target = new TabArea(dndManager);
                target.add(view, ViewPosition.CENTER);
                split(target, this, Orientation.VERTICAL);
            }
            break;
        case BOTTOM:
            if (orientation == Orientation.VERTICAL) {
                getSecondChild().add(view, position);
            } else {
                ViewArea target = new TabArea(dndManager);
                target.add(view, ViewPosition.CENTER);
                split(this, target, Orientation.VERTICAL);
            }
            break;
        case LEFT:
            if (orientation == Orientation.HORIZONTAL) {
                getSecondChild().add(view, position);
            } else {
                ViewArea target = new TabArea(dndManager);
                target.add(view, ViewPosition.CENTER);
                split(target, this, Orientation.HORIZONTAL);
            }
            break;
        case RIGHT:
            if (orientation == Orientation.HORIZONTAL) {
                getSecondChild().add(view, position);
            } else {
                ViewArea target = new TabArea(dndManager);
                target.add(view, ViewPosition.CENTER);
                split(this, target, Orientation.HORIZONTAL);
            }
            break;
        }
        view.getArea().getNode().requestLayout();
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
            getParent().replace(this, secondChild);
        } else if (area == secondChild) {
            getParent().replace(this, firstChild);
        }
    }

    /**
     * Replace the {@param oldArea} with the {@param newArea}.
     *
     * @param oldArea The old area.
     * @param newArea The new area.
     */
    protected final void replace(ViewArea oldArea, ViewArea newArea) {
        if (oldArea == firstChild) {
            setFirstChild(newArea);
        } else if (oldArea == secondChild) {
            setSecondChild(newArea);
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
    protected void split(ViewArea first, ViewArea second, Orientation orientation) {
        if (!(first == this ^ second == this)) {
            throw new IllegalArgumentException("Either first or second area must be this.");
        }

        ViewArea area = new ViewArea(parent, dndManager);
        parent.replace(this, area);
        area.setOrientation(orientation);
        area.setFirstChild(first);
        area.setSecondChild(second);
        area.setEditor(area.getFirstChild().isEditor() || area.getSecondChild().isEditor());
    }

    /**
     * Get that child that is defined as editor
     *
     * @return The editor area.
     */
    private ViewArea getEditorArea() {
        if (getFirstChild().isEditor()) {
            return getFirstChild();
        } else if (getSecondChild().isEditor()) {
            return getSecondChild();
        }
        return null;
    }

    /**
     * Is this area or one of its childs the editor area.
     *
     * @return True if this or one of the childs is the editor area.
     */
    public final boolean isEditor() {
        return editor;
    }

    /**
     * Define this area as editor area.
     *
     * @param editor Is this area the editor area.
     */
    public final void setEditor(boolean editor) {
        this.editor = editor;
    }

    /**
     * Get the the root area where this view is registered.
     *
     * @return The root area of this view.
     */
    public final RootArea getRootArea() {
        ViewArea parent = this;
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        return (RootArea) parent;
    }

    /**
     * Set the orientation of the split area.
     *
     * @param orientation The orientation of splitting.
     */
    private void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        outerPane.setOrientation(orientation);
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