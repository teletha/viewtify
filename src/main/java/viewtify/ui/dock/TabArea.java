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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

import kiss.I;

/**
 * Describes a logical view area which displays the views within a tab pane.
 */
class TabArea extends ViewArea<TabPane> {

    /**
     * Create a new tab area.
     */
    TabArea() {
        super(new TabPane());

        node.addEventHandler(DragEvent.DRAG_OVER, e -> DockSystem.onDragOver(e, this));
        node.addEventHandler(DragEvent.DRAG_EXITED, e -> DockSystem.onDragExited(e, this));
        node.addEventHandler(DragEvent.DRAG_DONE, e -> DockSystem.onDragDone(e, this));
        node.addEventHandler(DragEvent.DRAG_DROPPED, e -> DockSystem.onDragDropped(e, this));
        node.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            I.signal(node.lookupAll(".tab"))
                    .take(tab -> tab.localToScene(tab.getBoundsInLocal()).contains(e.getSceneX(), e.getSceneY()))
                    .first()
                    .to(tab -> {
                        DockSystem.onDragDetected(e, this, node.getSelectionModel().getSelectedItem());
                    });
        });
    }

    /**
     * Remove a view from this area. If this area is empty it will also be removed.
     *
     * @param view The view to remove
     */
    void remove(Tab view) {
        remove(view, true);
    }

    /**
     * Remove a view from this area. If checkEmpty is true it checks if this area is empty and
     * remove this area.
     *
     * @param view The view to remove.
     * @param checkEmpty Should this area be removed if it is empty?
     */
    void remove(Tab view, boolean checkEmpty) {
        node.getTabs().remove(view);
        if (checkEmpty) {
            handleEmpty();
        }
    }

    /**
     * Check if this area is empty, so remove it.
     */
    void handleEmpty() {
        if (node.getTabs().isEmpty()) {
            parent.remove(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void add(Tab view, int position) {
        switch (position) {
        case DockSystem.CENTER:
            node.getTabs().add(view);
            view.setOnCloseRequest(e -> remove(view));
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
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
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