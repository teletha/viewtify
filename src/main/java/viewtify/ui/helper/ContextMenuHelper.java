/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import viewtify.ui.UIContextMenu;

public interface ContextMenuHelper<Self extends ContextMenuHelper> extends PropertyAccessHelper {

    /**
     * Create context menu.
     * 
     * @param builder
     * @return
     */
    default Self context(Consumer<UIContextMenu> builder) {
        Property<ContextMenu> holder = property(Type.ContextMenu);

        ContextMenu menus = holder.getValue();
        if (menus == null) {
            menus = createEnhancedContextMenu();
            holder.setValue(menus);
        }

        // separate for each context assigners
        ObservableList<MenuItem> items = menus.getItems();
        if (!items.isEmpty()) {
            items.add(new SeparatorMenuItem());
        }

        // build context menus
        builder.accept(new UIContextMenu(menus));

        // API definition
        return (Self) this;
    }

    /**
     * Enahnce context menu's behavior.
     * 
     * @return menu The new created {@link ContextMenu}.
     */
    private ContextMenu createEnhancedContextMenu() {
        ContextMenu menu = new ContextMenu();
        menu.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<>() {

            @Override
            public void handle(WindowEvent x) {
                menu.removeEventHandler(WindowEvent.WINDOW_SHOWING, this);

                for (MenuItem item : menu.getItems()) {
                    Node node = item.getStyleableNode();
                    if (node != null) {
                        /**
                         * When the mouse cursor moves outside the menu item, the focus is released
                         * from the menu item by requesting focus to another node.
                         */
                        node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                            menu.getStyleableNode().requestFocus();
                        });

                        /**
                         * When the mouse button pressed on a new item is released, the mouse event
                         * is consumed if the menu item is outside. This can prevent the menu item
                         * from being triggered.
                         */
                        node.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
                            if (!node.contains(e.getX(), e.getY())) {
                                e.consume();
                                menu.hide();
                            }
                        });
                    }
                }
            }
        });
        return menu;
    }
}
