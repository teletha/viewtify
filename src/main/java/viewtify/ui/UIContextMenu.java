/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;

public class UIContextMenu {

    /** The actual ui. */
    private final ContextMenu ui;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    public UIContextMenu(ContextMenu ui) {
        this.ui = ui;
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
        CustomMenuItem item = new CustomMenuItem(provider.ui());
        item.setHideOnClick(hideOnClick);

        ui.getItems().add(item);
    }

    /**
     * Declare simple menu.
     * 
     * @return
     */
    public UIMenuItem menu() {
        MenuItem menu = new MenuItem();
        ui.getItems().add(menu);

        return new UIMenuItem(menu);
    }

    /**
     * Declare checkbox menu.
     * 
     * @return
     */
    public UIMenuItem checkMenu() {
        CheckMenuItem menu = new CheckMenuItem();
        ui.getItems().add(menu);

        return new UIMenuItem(menu);
    }
}
