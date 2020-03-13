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
    private final boolean canCloseStage;

    /**
     * Create a new root area.
     *
     * @param box Use this pane to draw all the content.
     * @param dndManager The drag&drop manager
     * @param canCloseStage Close the stage containing this area when the last view was removed?
     */
    RootArea(boolean canCloseStage) {
        super(new HBox());

        this.canCloseStage = canCloseStage;

        setFirstChild(new TabArea());
    }

    /**
     * Set {@param child} as first child of this view area.
     * <p/>
     * It will also update the javafx scene graph and the childs parent value.
     *
     * @param child The new child.
     */
    @Override
    protected void setFirstChild(ViewArea child) {
        super.setFirstChild(child);

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
        firstChild.add(view, position);
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