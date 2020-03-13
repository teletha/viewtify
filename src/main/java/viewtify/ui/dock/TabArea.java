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

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import kiss.I;

/**
 * Describes a logical view area which displays the views within a tab pane.
 */
class TabArea extends ViewArea<TabPane> {

    /** A list with all contained views. */
    private final Set<Tab> views = new LinkedHashSet<>();

    /**
     * Create a new tab area.
     */
    TabArea() {
        super(new TabPane());

        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            I.signal(node.lookupAll(".tab"))
                    .take(tab -> tab.localToScene(tab.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY()))
                    .first()
                    .to(tab -> {
                        DockSystem.onDragDetected(event, node.getSelectionModel().getSelectedItem());
                    });
        });
        node.setOnDragDone(event -> {
            DockSystem.onDragDone(event);
        });
        registerDragEvents(node);
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
        if (!views.contains(view)) {
            return;
        }
        views.remove(view);
        node.getTabs().remove(view);
        if (checkEmpty) {
            handleEmpty();
        }
    }

    /**
     * Check if this area is empty, so remove it.
     */
    void handleEmpty() {
        if (views.isEmpty()) {
            parent.remove(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void add(Tab view, ViewPosition position) {
        if (position != ViewPosition.CENTER) {
            super.add(view, position);
            return;
        }
        views.add(view);
        node.getTabs().add(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canDropToCenter() {
        return true;
    }

    static TabArea of(Tab tab) {
        return (TabArea) tab.getTabPane().getUserData();
    }
}