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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

/**
 * Describes a logical view area which displays the views within a tab pane.
 */
public class TabArea extends ViewArea {

    private final TabPane tabPane = new TabPane();

    /**
     * A list with all contained views.
     */
    private final Set<ViewStatus> views = new LinkedHashSet<>();

    /**
     * Create a new tab area.
     *
     * @param dragNDropManager Use this drag&drop manager to handle the view management.
     */
    public TabArea(DNDManager dragNDropManager) {
        super(dragNDropManager);
        registerDragEvents();

        initFocusEvents();
    }

    /**
     * Create a new tab area.
     *
     * @param parent Use this area as parent area.
     * @param dragNDropManager Use this drag&drop manager to handle the view management.
     */
    public TabArea(ViewArea parent, DNDManager dragNDropManager) {
        super(parent, dragNDropManager);
        registerDragEvents();
        initFocusEvents();
    }

    /**
     * Initialize the focused events. This event handlers handles all the events which would change
     * the current focused view.
     */
    private void initFocusEvents() {
        tabPane.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old, Boolean newValue) {
                if (newValue && tabPane.getSelectionModel().getSelectedItem() != null) {
                    ViewStatus status = (ViewStatus) tabPane.getSelectionModel().getSelectedItem().getUserData();
                    getDragNDropManager().windowManager.setFocusedView(status.getView());
                }
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
                if (newTab != null) {
                    ViewStatus status = (ViewStatus) newTab.getUserData();
                    getDragNDropManager().windowManager.setFocusedView(status.getView());
                }
            }
        });
    }

    /**
     * Register the event handler for drag&drop of views.
     */
    private void registerDragEvents() {
        tabPane.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getDragNDropManager().onDragDetected(event);
            }
        });
        tabPane.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                getDragNDropManager().onDragDone(event);
            }
        });
        super.registerDragEvents(tabPane);
    }

    /**
     * Remove a view from this area. If this area is empty it will also be removed.
     *
     * @param view The view to remove
     */
    public void remove(ViewStatus view) {
        remove(view, true);
    }

    /**
     * Remove a view from this area. If checkEmpty is true it checks if this area is empty and
     * remove this area.
     *
     * @param view The view to remove.
     * @param checkEmpty Should this area be removed if it is empty?
     */
    public void remove(ViewStatus view, boolean checkEmpty) {
        if (!views.contains(view)) {
            return;
        }
        views.remove(view);
        view.setArea(null);
        view.setPosition(null);
        tabPane.getTabs().remove(view.getTab());
        if (checkEmpty) {
            handleEmpty();
        }
    }

    /**
     * Check if this area is empty, so remove it.
     *
     * @return True if this area is empty and was successfully removed.
     */
    public boolean handleEmpty() {
        if (views.isEmpty() && (!isEditor() || getRootArea().isCloseStage())) {
            getParent().remove(this);
            return true;
        }
        return false;
    }

    /**
     * Get the javafx scene graph node which represents this area.
     *
     * @return The scene graph node.
     */
    @Override
    public Parent getNode() {
        return tabPane;
    }

    /**
     * Add the view to this area.
     *
     * @param view The view to add.
     * @param position Add the view at this position.
     */
    @Override
    public void add(ViewStatus view, Position position) {
        if (position != Position.CENTER) {
            super.add(view, position);
            return;
        }
        views.add(view);
        view.setArea(this);
        view.setPosition(position);
        tabPane.getTabs().add(view.getTab());
    }

    /**
     * Is the drop gesture to this area with position center allowed?
     *
     * @return True if a drop to center is allowed.
     */
    @Override
    public boolean dropToCenter() {
        return true;
    }
}