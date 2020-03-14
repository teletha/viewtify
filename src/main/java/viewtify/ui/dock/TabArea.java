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
     * {@inheritDoc}
     */
    @Override
    void remove(ViewArea area) {
        // do nothing
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
    protected void add(Tab tab, int position) {
        if (position != DockSystem.CENTER) {
            super.add(tab, position);
        } else {
            node.getTabs().add(tab);
            tab.setOnCloseRequest(e -> remove(tab));
        }
    }
}