/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * @version 2017/11/15 9:54:15
 */
public class UIContextMenu {

    /** The actual ui. */
    public final ContextMenu ui;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    UIContextMenu(ContextMenu ui) {
        this.ui = ui;
    }

    /**
     * Add menu items.
     * 
     * @param item
     * @return
     */
    public UIContextMenu item(UIMenuItem... items) {
        for (UIMenuItem item : items) {
            ui.getItems().add(item.ui);
        }
        return this;
    }

    /**
     * @param string
     * @return
     */
    public UIMenuItem menu(String label) {
        MenuItem menu = new MenuItem(label);
        ui.getItems().add(menu);

        return new UIMenuItem(menu);
    }
}
