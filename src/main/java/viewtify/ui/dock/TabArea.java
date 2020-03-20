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

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.StackPane;

import kiss.I;
import kiss.Variable;
import viewtify.Key;
import viewtify.Viewtify;
import viewtify.ui.UITab;
import viewtify.ui.UITabPane;
import viewtify.ui.helper.User;

/**
 * Describes a logical view area which displays the views within a tab pane.
 */
class TabArea extends ViewArea<UITabPane> {

    /** The selected id. */
    private String selected;

    /** The initial selected id. We are doing a very tricky code to restore the tab selection. */
    private String selectedInitial;

    /** The initial view id manager. */
    private List<String> viewInitial = new ArrayList();

    /** The header node. */
    private final StackPane header;

    /**
     * Create a new tab area.
     */
    TabArea() {
        this(null);
    }

    /**
     * Create a new tab area with the specified {@link TabPane}.
     */
    TabArea(UITabPane pane) {
        super(pane != null ? pane : new UITabPane(null));

        saveSelectedTab();
        node.style("stop-anime");
        node.when(User.DragOver, e -> DockSystem.onDragOver(e, this));
        node.when(User.DragEnter, e -> DockSystem.onDragEntered(e, this));
        node.when(User.DragExit, e -> DockSystem.onDragExited(e, this));
        node.when(User.DragDrop, e -> DockSystem.onDragDropped(e, this));
        node.when(User.DragFinish, e -> DockSystem.onDragDone(e, this));
        node.when(User.DragStart, e -> {
            I.signal(node.ui.getTabs())
                    .map(tab -> tab.getStyleableNode())
                    .take(tab -> tab.localToScene(tab.getBoundsInLocal()).contains(e.getSceneX(), e.getSceneY()))
                    .first()
                    .to(tab -> {
                        DockSystem.onDragDetected(e, this, (UITab) node.ui.getSelectionModel().getSelectedItem());
                    });
        });

        // Since TabPane implementation delays the initialization of Skin and internal nodes
        // are not generated. So we should create Skin eagerly.
        TabPaneSkin skin = new TabPaneSkin(node.ui);
        node.ui.setSkin(skin);

        header = (StackPane) node.ui.lookup(".tab-header-area");
        header.addEventHandler(DragEvent.DRAG_ENTERED, e -> DockSystem.onHeaderDragEntered(e, this));
        header.addEventHandler(DragEvent.DRAG_EXITED, e -> DockSystem.onHeaderDragExited(e, this));
        header.addEventHandler(DragEvent.DRAG_DROPPED, e -> DockSystem.onHeaderDragDropped(e, this));
        header.addEventHandler(DragEvent.DRAG_OVER, e -> DockSystem.onHeaderDragOver(e, this));
        node.when(User.input(Key.Alt)).take(() -> node.items().size() == 1).to(e -> setHeader(!header.isVisible()));
    }

    /**
     * Get the header property of this {@link TabArea}.
     * 
     * @return The header property.
     */
    @SuppressWarnings("unused")
    private final boolean isHeader() {
        return node.isHeaderShown();
    }

    /**
     * Set the header property of this {@link TabArea}.
     * 
     * @param show The header value to set.
     */
    private final void setHeader(boolean show) {
        Platform.runLater(() -> {
            node.showHeader(show);
            DockSystem.requestSavingLayout();
        });
    }

    /**
     * Get the ids property of this {@link TabArea}.
     * 
     * @return The ids property.
     */
    @SuppressWarnings("unused")
    private final List<String> getIds() {
        return I.signal(node.ui.getTabs()).map(Tab::getId).toList();
    }

    /**
     * Set the ids property of this {@link TabArea}.
     * 
     * @param ids The ids value to set.
     */
    @SuppressWarnings("unused")
    private final void setIds(List<String> ids) {
        this.viewInitial = ids;
    }

    /**
     * Get the selected property of this {@link TabArea}.
     * 
     * @return The selected property.
     */
    @SuppressWarnings("unused")
    private String getSelected() {
        return selected;
    }

    /**
     * Set the selected property of this {@link TabArea}.
     * 
     * @param value The selected value to set.
     */
    @SuppressWarnings("unused")
    private void setSelected(String value) {
        // The selctedInitial field is guaranteed to be initialized only once.
        // This initialization should only be performed when restoring from a configuration file.
        if (selectedInitial == null && selected == null) selectedInitial = value;
        this.selected = value;
    }

    /**
     * Select if the specified tab must be selected at initialization. This selection is guaranteed
     * to be performed only once.
     * 
     * @param tab
     */
    private void selectInitialTabOnlyOnce(Tab tab) {
        if (selectedInitial != null && Objects.equals(tab.getId(), selectedInitial)) {
            node.ui.getSelectionModel().select(tab);
            selectedInitial = null;
        }
    }

    /**
     * Save the current selected tab countinuously.
     */
    private void saveSelectedTab() {
        Viewtify.observe(node.ui.getSelectionModel().selectedItemProperty()).to(tab -> {
            if (tab != null) {
                selected = tab.getId();
                DockSystem.requestSavingLayout();
            }
        });
    }

    /**
     * Remove a view from this area. If checkEmpty is true it checks if this area is empty and
     * remove this area.
     *
     * @param tab The view to remove.
     * @param checkEmpty Should this area be removed if it is empty?
     */
    void remove(Tab tab, boolean checkEmpty) {
        node.ui.getTabs().remove(tab);
        if (checkEmpty) {
            handleEmpty();
        }
    }

    /**
     * Check if this area is empty, so remove it.
     */
    void handleEmpty() {
        if (node.ui.getTabs().isEmpty()) {
            parent.remove(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(UITab tab, int position) {
        if (position == DockSystem.PositionRestore) {
            ObservableList<Tab> items = node.ui.getTabs();
            position = items.size();

            for (int i = 0; i < position; i++) {
                Tab item = items.get(i);

                if (compare(item.getId(), tab.getId())) {
                    position = i;
                    break;
                }
            }
        }

        switch (position) {
        case DockSystem.PositionTop:
        case DockSystem.PositionBottom:
        case DockSystem.PositionLeft:
        case DockSystem.PositionRight:
            super.add(tab, position);
            break;

        case DockSystem.PositionCenter:
            position = node.ui.getTabs().size();
            // fall-through

        default:
            node.ui.getTabs().add(position, tab);
            tab.setOnCloseRequest(e -> remove(tab, true));

            selectInitialTabOnlyOnce(tab);
            break;
        }
    }

    /**
     * Compare tab order by id.
     * 
     * @param tester
     * @param test
     * @return
     */
    private boolean compare(String tester, String test) {
        for (String id : viewInitial) {
            if (id.equals(tester)) {
                return false;
            } else if (id.equals(test)) {
                return true;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Variable<ViewArea> findAreaBy(String id) {
        return Variable.of(viewInitial.contains(id) ? this : null);
    }
}