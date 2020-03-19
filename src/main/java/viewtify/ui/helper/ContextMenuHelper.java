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

import java.util.Iterator;
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
        return context(this, builder);
    }

    /**
     * Add a context menu. If the specified ID already exists, the menu will be overwritten instead
     * of added.
     * 
     * @param id An identifier of context menu.
     * @param builder
     * @return
     */
    default Self context(Object id, Consumer<UIContextMenu> builder) {
        removeContext(id);

        Property<ContextMenu> holder = property(Type.ContextMenu);
        ContextMenu menus = holder.getValue();
        if (menus == null) {
            menus = createEnhancedContextMenu();
            holder.setValue(menus);
        }

        // separate for each context assigners
        ObservableList<MenuItem> items = menus.getItems();

        // remove previous menu
        removeBy(id, items);

        // check separator
        if (!items.isEmpty()) {
            SeparatorMenuItem separator = new SeparatorMenuItem();
            separator.getProperties().put(id, null);
            items.add(separator);
        }

        // build context menus
        builder.accept(new UIContextMenu(id, menus));

        // API definition
        return (Self) this;
    }

    /**
     * Deletes all context menus associated with the this instance.
     * 
     * @return Chainable API.
     */
    default Self removeContext() {
        return removeContext(this);
    }

    /**
     * Deletes all context menus associated with the specified ID.
     * 
     * @param id An identifier of context menu.
     * @return Chainable API.
     */
    default Self removeContext(Object id) {
        Property<ContextMenu> holder = property(Type.ContextMenu);
        ContextMenu menus = holder.getValue();

        if (menus != null) {
            removeBy(id, menus.getItems());
        }
        return (Self) this;
    }

    /**
     * Remove context menus by ID.
     * 
     * @param id An identifier of context menu.
     * @param menus A list of context menu.
     */
    private void removeBy(Object id, ObservableList<MenuItem> menus) {
        if (menus != null) {
            Iterator<MenuItem> iterator = menus.iterator();
            while (iterator.hasNext()) {
                MenuItem menuItem = iterator.next();
                if (menuItem.getProperties().containsKey(id)) {
                    iterator.remove();
                }
            }
        }
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
