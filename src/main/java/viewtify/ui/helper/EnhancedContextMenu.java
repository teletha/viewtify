/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

final class EnhancedContextMenu extends ContextMenu implements EventHandler<ContextMenuEvent> {

    /** The disable state. */
    boolean disable;

    long lastHidden;

    /**
     * 
     */
    EnhancedContextMenu() {
        addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> lastHidden = System.currentTimeMillis());
        addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<>() {

            @Override
            public void handle(WindowEvent x) {
                removeEventHandler(WindowEvent.WINDOW_SHOWING, this);

                for (MenuItem item : getItems()) {
                    Node node = item.getStyleableNode();
                    if (node != null) {
                        /**
                         * When the mouse cursor moves outside the menu item, the focus is released
                         * from the menu item by requesting focus to another node.
                         */
                        node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                            getStyleableNode().requestFocus();
                        });

                        /**
                         * When the mouse button pressed on a new item is released, the mouse event
                         * is consumed if the menu item is outside. This can prevent the menu item
                         * from being triggered.
                         */
                        node.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
                            if (!node.contains(e.getX(), e.getY())) {
                                e.consume();
                                hide();
                            }
                        });
                    }
                }
            }
        });
    }

    boolean canShow() {
        // availability
        if (disable) {
            return false;
        }

        // check squencial access
        if (System.currentTimeMillis() - lastHidden < 100) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(ContextMenuEvent event) {
        System.out.println("STOP");
        event.consume();
    }

    /**
     * @param anchor
     */
    void assign(Node anchor) {
        anchor.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this);
    }
}