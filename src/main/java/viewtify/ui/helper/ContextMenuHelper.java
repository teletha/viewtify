/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventTarget;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import kiss.Variable;
import kiss.WiseRunnable;
import viewtify.ui.UIContextMenu;
import viewtify.ui.UserInterfaceProvider;

public interface ContextMenuHelper<Self extends ContextMenuHelper> extends PropertyAccessHelper {

    /**
     * Enahnce context menu's behavior.
     * 
     * @return menu The new created {@link ContextMenu}.
     */
    private EnhancedContextMenu context() {
        Property<ContextMenu> property = property(Type.ContextMenu);
        EnhancedContextMenu menu = (EnhancedContextMenu) property.getValue();
        if (menu == null) {
            menu = new EnhancedContextMenu();
            property.setValue(menu);
        }
        return menu;
    }

    /**
     * Defines the context menu for the UI component. The menu is not generated until it is actually
     * needed. This method allows you to specify a context menu using a builder function.
     *
     * @param builder A consumer that builds the UIContextMenu for the context menu.
     * @return A chainable API for further configuration.
     */
    default Self context(Consumer<UIContextMenu> builder) {
        return context(this, true, builder);
    }

    /**
     * Defines the context menu with the specified ID for the UI component. The menu is not
     * generated until it is actually needed. If the specified ID already exists, the menu will be
     * overwritten instead of added. This method allows you to specify a context menu using a
     * builder function.
     *
     * @param id An identifier for the context menu.
     * @param builder A consumer that builds the UIContextMenu for the context menu.
     * @return A chainable API for further configuration.
     */
    default Self context(Object id, Consumer<UIContextMenu> builder) {
        return context(id, true, builder);
    }

    /**
     * Define the context menu with the option to specify whether it should be generated lazily.
     * This method allows you to specify a context menu using a builder function.
     *
     * @param rebuildable If true, the context menu will be generated on every popup.
     * @param builder A consumer that builds the UIContextMenu for the context menu.
     * @return A chainable API for further configuration.
     */
    default Self context(boolean rebuildable, Consumer<UIContextMenu> builder) {
        return context(this, builder);
    }

    /**
     * Define the context menu with the option to specify whether it should be generated lazily. If
     * the specified ID already exists, the menu will be overwritten instead of added. This method
     * allows you to specify a context menu using a builder function.
     * 
     * @param id An identifier for the context menu.
     * @param rebuildable If true, the context menu will be generated on every popup.
     * @param builder A consumer that builds the UIContextMenu for the context menu.
     * @return A chainable API for further configuration.
     */
    default Self context(Object id, boolean rebuildable, Consumer<UIContextMenu> builder) {
        EnhancedContextMenu menus = context();

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
        if (!rebuildable) {
            // eager setup
            builder.accept(new UIContextMenu(id, menus.getItems()));
        } else {
            // lazy setup
            menus.addEventFilter(Menu.ON_SHOWING, e -> {
                int index = 0;
                ObservableList<MenuItem> children = menus.getItems();

                // remove old menus
                for (int i = children.size() - 1; 0 <= i; i--) {
                    MenuItem child = children.get(i);
                    if (child.getProperties().containsKey(id)) {
                        children.remove(i);
                        index = i;
                    }
                }

                // add new menus
                List<MenuItem> container = new ArrayList();
                builder.accept(new UIContextMenu(id, container));
                container.forEach(menu -> menu.getProperties().put(id, null));
                children.addAll(index, container);
            });

            // create dummy context
            MenuItem menu = new MenuItem("");
            menu.getProperties().put(id, null);
            menus.getItems().add(menu);
        }

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
                ObservableMap props = iterator.next().getProperties();
                if (props.remove(id, null)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Show the associated context menu.
     * 
     * @return
     */
    default Self showContext() {
        return showContext(null, 0, 0);
    }

    /**
     * Shows the {@code ContextMenu} relative to the side specified by the {@code side} parameter,
     * and offset by the given {@code dx} and {@code dy} values for the x-axis and y-axis,
     * respectively. If there is not enough room, the menu is moved to the opposite side and the
     * offset is not applied.
     * <p>
     * To clarify the purpose of the {@code side} parameter, consider that it is relative to the
     * anchor node. As such, a {@code side} of {@code TOP} would mean that the ContextMenu's bottom
     * left corner is set to the top left corner of the anchor.
     * <p>
     * This function is useful for finely tuning the position of a menu, relative to the parent node
     * to ensure close alignment.
     * 
     * @param location the side
     * @param dx the dx value for the x-axis
     * @param dy the dy value for the y-axis
     */
    default Self showContext(Side location, double dx, double dy) {
        if (this instanceof Node node) {
            return showContextOn(node, location, dx, dy);
        }

        if (this instanceof UserInterfaceProvider provider && provider.ui() instanceof Node node) {
            return showContextOn(node, location, dx, dy);
        }

        return (Self) this;
    }

    /**
     * Show the associated context menu.
     * 
     * @return
     */
    private Self showContextOn(Node anchor, Side location, double dx, double dy) {
        location = Objects.requireNonNullElse(location, Side.BOTTOM);
        context().show(anchor, location, dx, dy);

        return (Self) this;
    }

    /**
     * Hide the associated context menu.
     * 
     * @return
     */
    default Self hideContext() {
        ContextMenu context = property(Type.ContextMenu).getValue();
        if (context != null) {
            context.hide();
        }
        return (Self) this;
    }

    /**
     * Show the associated context menu.
     * 
     * @return
     */
    default Self toggleContext() {
        return toggleContext(null, 0, 0);
    }

    /**
     * Toggle the context menu.
     * 
     * @param location the side
     * @param dx the dx value for the x-axis
     * @param dy the dy value for the y-axis
     */
    default Self toggleContext(Side location, double dx, double dy) {
        if (isContextShowing()) {
            return hideContext();
        } else {
            return showContext(location, dx, dy);
        }
    }

    /**
     * Whether or not the context menu is showing (that is, open on the user's system). The context
     * menu might be "showing", yet the user might not be able to see it due to the context menu
     * being rendered behind another context menu or due to the context menu being positioned off
     * the monitor.
     * 
     * @return A result.
     */
    default boolean isContextShowing() {
        ContextMenu context = property(Type.ContextMenu).getValue();
        return context != null && context.isShowing();
    }

    /**
     * Change the control of showing and hiding the context menu from right-click to left-click and
     * ignore native context requesting.
     * 
     * @return
     */
    default Self behaveLikeButton() {
        target(this).to(x -> x.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                toggleContext(Side.BOTTOM, -85, 0);
            }
        }));
        return disableNativeContextRequest();
    }

    /**
     * Enable the context menu.
     * 
     * @return
     */
    default Self enableContext() {
        context().disable = false;

        return (Self) this;
    }

    /**
     * Enable the context menu.
     * 
     * @return
     */
    default Self disableContext() {
        context().disable = true;

        return (Self) this;
    }

    /**
     * Enable the context menu.
     * 
     * @return
     */
    default Self enableNativeContextRequest() {
        target(this).to(x -> x.removeEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, EnhancedContextMenu.NOOP));

        return (Self) this;
    }

    /**
     * Disable the context menu.
     * 
     * @return
     */
    default Self disableNativeContextRequest() {
        target(this).to(x -> x.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, EnhancedContextMenu.NOOP));

        return (Self) this;
    }

    /**
     * Wrap by {@link UserActionHelper}.
     * 
     * @param o
     * @return
     */
    private Variable<EventTarget> target(Object o) {
        if (o instanceof EventTarget target) {
            return Variable.of(target);
        } else if (o instanceof UserInterfaceProvider provider && provider.ui() instanceof EventTarget target) {
            return Variable.of(target);
        }
        return Variable.empty();
    }

    /**
     * Hook context event.
     * 
     * @return
     */
    default Self hookContext(WiseRunnable beforeShowing, WiseRunnable afterHidden) {
        if (beforeShowing != null) {
            context().addEventHandler(WindowEvent.WINDOW_SHOWING, e -> beforeShowing.run());
        }
        if (afterHidden != null) {
            context().addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> afterHidden.run());
        }
        return (Self) this;
    }
}