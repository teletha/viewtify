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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * A RootArea is a special {@link ViewArea} which has no parent and is directly used as root.
 */
final class RootArea extends ViewArea<HBox> {

    /** Close the stage containing this area when removing the child. */
    private boolean canCloseStage;

    /**
     * Create a new root area.
     *
     * @param box Use this pane to draw all the content.
     * @param dndManager The drag&drop manager
     * @param canCloseStage Close the stage containing this area when the last view was removed?
     */
    RootArea() {
        super(new HBox());
    }

    /**
     * Get the canCloseStage property of this {@link RootArea}.
     * 
     * @return The canCloseStage property.
     */
    final boolean isCanCloseStage() {
        return canCloseStage;
    }

    /**
     * Set the canCloseStage property of this {@link RootArea}.
     * 
     * @param canCloseStage The canCloseStage value to set.
     */
    final void setCanCloseStage(boolean canCloseStage) {
        this.canCloseStage = canCloseStage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChild(int index, ViewArea child) {
        super.setChild(index, child);

        if (node.getChildren().isEmpty()) {
            node.getChildren().add(child.node);
        } else {
            node.getChildren().set(0, child.node);
        }
        HBox.setHgrow(child.node, Priority.ALWAYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void add(Tab view, int position) {
        if (children.isEmpty()) {
            setChild(0, new TabArea());
        }
        children.get(0).add(view, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void remove(ViewArea area) {
        if (canCloseStage) {
            ((Stage) node.getScene().getWindow()).close();
        }
    }
}