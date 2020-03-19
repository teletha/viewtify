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

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;

import kiss.I;
import kiss.Variable;
import kiss.WiseRunnable;
import viewtify.ui.UITab;
import viewtify.ui.helper.User;

/**
 * Describes a logical view area which displays the views within a tab pane.
 */
class TileArea extends ViewArea<Pane> {

    /** The id manager. */
    public String id;

    /** The operation to remove content. */
    private WiseRunnable removePreviousContent = I.NoOP;

    /**
     * Create a new tab area.
     */
    TileArea() {
        super(new Pane());

        node.getChildren().add(new Pane());
        node.addEventHandler(DragEvent.DRAG_OVER, e -> DockSystem.onDragOver(e, this));
        node.addEventHandler(DragEvent.DRAG_ENTERED, e -> DockSystem.onDragEntered(e, this));
        node.addEventHandler(DragEvent.DRAG_EXITED, e -> DockSystem.onDragExited(e, this));
        node.addEventHandler(DragEvent.DRAG_DROPPED, e -> DockSystem.onDragDropped(e, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(UITab tab, ViewArea from, int position, boolean tabMode) {
        switch (position) {
        case DockSystem.PositionTop:
        case DockSystem.PositionBottom:
        case DockSystem.PositionLeft:
        case DockSystem.PositionRight:
            super.add(tab, from, position, tabMode);
            break;

        default:
            removePreviousContent.run();

            String id = tab.getId();
            String tiledId = "Tiled#" + id;
            Node content = tab.getContent();
            TabArea main = DockSystem.mainTabArea();

            removePreviousContent = () -> {
                tab.setId(id);
                tab.setDisable(false);
                tab.setContent(content);
                tab.removeContext();

                parent.remove(this);
                DockSystem.saveLayout();
            };

            tab.setId(tiledId);
            tab.setDisable(true);
            tab.setContent(null);
            tab.context(c -> {
                c.menu().text("Close").when(User.Action, removePreviousContent);
            });

            this.id = id;
            this.node.getChildren().set(0, content);

            main.add(tab, from, position, true);
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable<ViewArea> findAreaBy(String id) {
        return Variable.of(Objects.equals(this.id, id) ? this : null);
    }
}