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

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import viewtify.ui.UIHBox;
import viewtify.ui.UITab;
import viewtify.ui.UITabPane;

/**
 * A RootArea is a special {@link ViewArea} which has no parent and is directly used as root.
 */
final class RootArea extends ViewArea<UIHBox> {

    /** The dock window id. */
    public String id = "DockWindow-" + (int) (Math.random() * 100000000);

    /** Window Kind */
    public boolean sub;

    /**
     * Create a new root area.
     *
     * @param box Use this pane to draw all the content.
     * @param dndManager The drag&drop manager
     * @param canCloseStage Close the stage containing this area when the last view was removed?
     */
    RootArea() {
        this(null);
    }

    RootArea(UITabPane root) {
        super(new UIHBox(null));
        
        if (root != null) {
            setChild(0, new TabArea(root));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        if (node.ui.getChildren().isEmpty()) {
            node.ui.getChildren().add(child.node.ui);
        } else {
            node.ui.getChildren().set(0, child.node.ui);
        }
        HBox.setHgrow(child.node.ui, Priority.ALWAYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(UITab tab, int position) {
        if (children.isEmpty()) {
            setChild(0, new TabArea());
        }
        children.get(0).add(tab, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(ViewArea area) {
        if (sub) {
            DockSystem.requestCloseWindow(this);
        }
    }
}