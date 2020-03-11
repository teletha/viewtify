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

import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;

import viewtify.ui.View;

/**
 * Stores the current status and additional metadata of an window manager view.
 */
final class ViewStatus {

    /** The registered view */
    final View view;

    private final ViewPosition defaultPosition;

    final ViewStatus parent;

    /** The tab which contains this view. */
    final Tab tab;

    /** The status whether this view is visible or hidden (or something else) */
    private Status status;

    /** The current position within the window */
    private ViewPosition position;

    private TabArea area;

    /**
     * Create a new view status.
     *
     * @param view The view to display.
     */
    ViewStatus(View view) {
        this(view, null);
    }

    /**
     * Create a new view status from a view with the given parent to define the exact position for
     * this view.
     *
     * @param view The view to display.
     * @param parent The parent view status for exact positioning.
     */
    ViewStatus(View view, ViewStatus parent) {
        this.view = view;
        this.position = ViewPosition.CENTER;
        this.defaultPosition = ViewPosition.CENTER;
        this.setStatus(Status.VISIBLE);
        this.parent = parent;

        tab = new Tab(view.id());
        tab.setClosable(true);
        tab.setContent(view.ui());
        tab.setId(view.id());
        tab.setUserData(this);
        tab.setOnClosed(event -> {
            ViewStatus status = ViewStatus.this;
            status.getArea().remove(status);
            status.setStatus(Status.HIDDEN);
        });
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ViewPosition getPosition() {
        return position;
    }

    public void setPosition(ViewPosition position) {
        this.position = position;
    }

    /**
     * Restore the defaults for this view.
     */
    public void restoreDefault() {
        status = Status.VISIBLE;
        position = defaultPosition;
    }

    public TabArea getArea() {
        return area;
    }

    public void setArea(TabArea area) {
        this.area = area;
    }

    /**
     * Resize the area of this view to the defined value.
     */
    public void setDeviderPositions() {
        SplitPane splitPane;
        final double space = 0.5;

        if (getArea().getParent().getNode() instanceof SplitPane) {
            splitPane = (SplitPane) getArea().getParent().getNode();
        } else {
            return;
        }

        if (space < 0.05 || space > 0.95) {
            return;
        }
        switch (position) {
        case LEFT: // fall trough
        case TOP:
            splitPane.setDividerPositions(space);
            break;
        case RIGHT: // all trough
        case BOTTOM:
            splitPane.setDividerPositions(1 - space);
            break;
        default:
            break;
        }
    }

    @Override
    public String toString() {
        return "ViewStatus{" + "view=" + view + ", parent=" + parent + ", status=" + status + ", position=" + position + '}';
    }

    /**
     * The status of a view.
     */
    enum Status {
        /**
         * Indicates that a view is visible as tab within a view area.
         */
        VISIBLE,

        /**
         * Indicates that a view is docked to the side.
         */
        DOCKED,

        /**
         * Indicates that the view is registered but neither docked or viewed.
         */
        HIDDEN,
    }
}