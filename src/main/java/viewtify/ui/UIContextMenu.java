/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import kiss.Variable;

public class UIContextMenu {

    /** The actual ui. */
    private final Supplier<ObservableList<MenuItem>> menuProvider;

    /** The identifier of context menu. */
    private final Object id;

    /**
     * Enchanced view.
     */
    public UIContextMenu(Object id, Supplier<ObservableList<MenuItem>> menuProvider) {
        this.id = id;
        this.menuProvider = menuProvider;
    }

    /**
     * Declare menu ui.
     * 
     * @param provider
     */
    public final void menu(UserInterfaceProvider<? extends Node> provider) {
        menu(provider, true);
    }

    /**
     * Declare menu ui.
     * 
     * @param provider
     */
    public final void menu(UserInterfaceProvider<? extends Node> provider, boolean hideOnClick) {
        CustomMenuItem item = assignID(new CustomMenuItem(provider.ui()));
        item.setHideOnClick(hideOnClick);

        menuProvider.get().add(item);
    }

    /**
     * Declare simple menu.
     * 
     * @return Chainable API.
     */
    public UIMenuItem menu() {
        MenuItem menu = assignID(new MenuItem());
        menu.getProperties().put(id, null);
        menuProvider.get().add(menu);

        return new UIMenuItem(menu);
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     * @return Chainable API.
     */
    public UIMenuItem menu(Object text) {
        return menu().text(text);
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     * @return Chainable API.
     */
    public UIMenuItem menu(Variable text) {
        return menu().text(text);
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     */
    public void menu(Object text, Consumer<UIContextMenu> sub) {
        Menu menu = assignID(new Menu(String.valueOf(text)));
        menu.getProperties().put(id, null);
        menu.setOnShowing(e -> {
            System.out.println("SHOW " + e);
        });
        menuProvider.get().add(menu);

        sub.accept(new UIContextMenu(text, menu::getItems));
    }

    /**
     * Declare checkbox menu.
     * 
     * @return Chainable API.
     */
    public UIMenuItem checkMenu() {
        CheckMenuItem menu = assignID(new CheckMenuItem());
        menuProvider.get().add(menu);

        return new UIMenuItem(menu);
    }

    /**
     * Declare menu separator.
     */
    public void separator() {
        menuProvider.get().add(new SeparatorMenuItem());
    }

    /**
     * Assign ID to menu.
     * 
     * @param item
     */
    private <M extends MenuItem> M assignID(M item) {
        item.getProperties().put(id, null);

        return item;
    }
}