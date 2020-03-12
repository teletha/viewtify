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

import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * A RootArea is a special {@link ViewArea} which has no parent and is directly used as root.
 */
final class RootArea extends ViewArea {

    /** The actual root node. */
    private final Pane box;

    /** Close the stage containing this area when removing the child. */
    private final boolean canCloseStage;

    /**
     * Create a new root area.
     *
     * @param box Use this pane to draw all the content.
     * @param dndManager The drag&drop manager
     * @param canCloseStage Close the stage containing this area when the last view was removed?
     */
    RootArea(DNDManager dndManager, boolean canCloseStage) {
        super(dndManager);
        this.box = new HBox();
        this.canCloseStage = canCloseStage;

        setFirstChild(new TabArea(dndManager));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parent getNode() {
        return box;
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

        if (box.getChildren().isEmpty()) {
            box.getChildren().add(child.getNode());
        } else {
            box.getChildren().set(0, child.getNode());
        }
        HBox.setHgrow(child.getNode(), Priority.ALWAYS);
    }

    /**
     * Add the view to this area at position.
     * <p/>
     * If position is {@link ViewPosition#CENTER} it will be added to that child that is defined as
     * editor area. Otherwise this area is split and the view will be positioned according the
     * position parameter.
     *
     * @param view The view to add.
     * @param position Add the view at this position.
     */
    @Override
    protected void add(ViewStatus view, ViewPosition position) {
        getFirstChild().add(view, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void remove(ViewArea area) {
        if (canCloseStage) {
            ((Stage) box.getScene().getWindow()).close();
        }
    }
}