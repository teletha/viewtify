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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;

import kiss.Variable;
import viewtify.ui.UITab;
import viewtify.ui.helper.User;

/**
 * Describes a logical view area which displays the views within a tab pane.
 */
class TileArea extends ViewArea<Pane> {

    /** The id manager. */
    public String id;

    /**
     * Create a new tab area.
     */
    TileArea() {
        super(new Pane());

        node.addEventHandler(DragEvent.DRAG_OVER, e -> DockSystem.onDragOver(e, this));
        node.addEventHandler(DragEvent.DRAG_ENTERED, e -> DockSystem.onDragEntered(e, this));
        node.addEventHandler(DragEvent.DRAG_EXITED, e -> DockSystem.onDragExited(e, this));
        node.addEventHandler(DragEvent.DRAG_DROPPED, e -> DockSystem.onDragDropped(e, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void add(UITab tab, int position) {
        switch (position) {
        case DockSystem.PositionTop:
        case DockSystem.PositionBottom:
        case DockSystem.PositionLeft:
        case DockSystem.PositionRight:
            super.add(tab, position);
            break;

        default:
            this.id = tab.getId();

            TabArea main = DockSystem.mainTabArea();
            tab.setId("Tiled#" + tab.getId());
            main.add(tab, position);

            Node content = tab.getContent();
            tab.setDisable(true);
            tab.context(TileArea.class, c -> {
                c.menu().text("Close").when(User.Action, () -> {
                    tab.setDisable(false);
                    tab.setContent(content);

                    int index = main.ids.indexOf(tab.getId());
                    main.ids.set(index, this.id);
                    tab.setId(this.id);

                    parent.remove(this);

                    tab.removeContext(TileArea.class);
                });
            });

            ObservableList<Node> items = node.getChildren();
            if (items.isEmpty()) {
                items.add(content);
            } else {
                items.set(0, content);
            }

            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Variable<ViewArea> findAreaBy(String id) {
        return Variable.of(Objects.equals(this.id, id) ? this : null);
    }
}