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

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import viewtify.ui.UIPane;
import viewtify.ui.UITab;
import viewtify.ui.UITabPane;

/**
 * A RootArea is a special {@link ViewArea} which has no parent and is directly used as root.
 */
final class RootArea extends ViewArea<UIPane> {

    /** The dock window name. */
    public String name = "DockWindow-" + (int) (Math.random() * 100000000);

    /** Window Kind */
    public boolean sub;

    /**
     * Create a new root area.
     */
    RootArea() {
        this(null);
    }

    /**
     * Create a new root area.
     */
    RootArea(UITabPane root) {
        super(new UIPane(new HBox(), null));
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
    public TabArea add(UITab tab, int position) {
        if (children.isEmpty()) {
            setChild(0, new TabArea());
        }
        return children.get(0).add(tab, position);
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