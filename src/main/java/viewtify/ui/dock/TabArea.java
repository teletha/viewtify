/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.StackPane;
import kiss.I;
import kiss.WiseConsumer;
import viewtify.Viewtify;
import viewtify.keys.Key;
import viewtify.ui.UILabel;
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
    private List<String> views = new ArrayList();

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
        node.when(User.input(Key.Alt)).take(v -> node.items().size() == 1).to(e -> {
            setHeader(!header.isVisible());
            DockSystem.requestSavingLayout();
        });

        node.ui.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> {
            if (n != null) {
                DockSystem.selected.set(n);
            }
        });

        node.ui.getTabs().addListener((ListChangeListener<Tab>) change -> {
            while (change.next()) {
                for (Tab tab : change.getRemoved()) {
                    handleTabManipulation(tab.getId(), false);
                }

                for (Tab tab : change.getAddedSubList()) {
                    handleTabManipulation(tab.getId(), true);
                }
            }
        });

        for (WiseConsumer<UILabel> builder : DockSystem.menuBuilders) {
            registerMenu(builder);
        }
    }

    /**
     * Handle tab removing.
     * 
     * @param id
     * @param checkEmpty
     */
    private void handleTabManipulation(String id, boolean add) {
        if (!add) {
            handleEmpty();
        }

        updatePosition();

        if (add) {
            DockSystem.openedTabs.add(id);
        } else {
            DockSystem.openedTabs.remove(id);
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
     * Update tab position.
     */
    private void updatePosition() {
        if (!DockSystem.whileRestration) {
            views = I.signal(node.ui.getTabs()).map(Tab::getId).toList();

            DockSystem.requestSavingLayout();
        }
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
        node.showHeader(show);
    }

    /**
     * Get the ids property of this {@link TabArea}.
     * 
     * @return The ids property.
     */
    final List<String> getIds() {
        return views;
    }

    /**
     * Set the ids property of this {@link TabArea}.
     * 
     * @param ids The ids value to set.
     */
    @SuppressWarnings("unused")
    private final void setIds(List<String> ids) {
        this.views = ids;
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
     */
    void remove(Tab tab) {
        node.ui.getTabs().remove(tab);
    }

    /**
     * Remove all views from this area.
     */
    void removeAll() {
        List<Tab> copies = node.ui.getTabs().stream().toList();
        for (Tab tab : copies) {
            remove(tab);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TabArea add(UITab tab, int position) {
        return add(tab, position, false);
    }

    TabArea add(UITab tab, int position, boolean select) {
        if (position == DockSystem.PositionRestore) {
            position = restorePosition(tab);
        }

        switch (position) {
        case DockSystem.PositionTop:
        case DockSystem.PositionBottom:
        case DockSystem.PositionLeft:
        case DockSystem.PositionRight:
            return super.add(tab, position);

        case DockSystem.PositionCenter:
            position = node.ui.getTabs().size();
            // fall-through

        default:
            node.ui.getTabs().add(position, tab);
            if (select) tab.select();

            selectInitialTabOnlyOnce(tab);
            return this;
        }
    }

    /**
     * Restore tab order by id.
     * 
     * @param tab
     * @return
     */
    private int restorePosition(UITab tab) {
        ObservableList<Tab> items = node.ui.getTabs();
        int size = items.size();

        for (int i = 0; i < size; i++) {
            Tab item = items.get(i);

            if (compare(item.getId(), tab.getId())) {
                return i;
            }
        }
        return size;
    }

    /**
     * Compare tab order by id.
     * 
     * @param tester
     * @param test
     * @return
     */
    private boolean compare(String tester, String test) {
        for (String id : views) {
            if (id.equals(tester)) {
                return false;
            } else if (id.equals(test)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasView(String id) {
        return views.contains(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        if (getIds().isEmpty()) {
            parent.remove(this);
        }
    }

    /**
     * Select tab by id.
     * 
     * @param id
     */
    void select(String id, boolean selectWindow) {
        for (UITab tab : node.items()) {
            if (tab.getId().equals(id)) {
                node.select(tab);

                if (selectWindow) {
                    // node.stage().ifPresent(Stage::toFront);
                }
                return;
            }
        }
    }

    /**
     * @param menuBuilder
     */
    void registerMenu(WiseConsumer<UILabel> menuBuilder) {
        node.registerIcon(label -> {
            menuBuilder.accept(label);

            label.hookContext(() -> {
                DockSystem.latestMenuActivatedTabArea = this;
            }, () -> {
                DockSystem.latestMenuActivatedTabArea = null;
            });
        });
    }
}