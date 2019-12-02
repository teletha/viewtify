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

import javafx.scene.control.ContextMenu;
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
     * Add menu item.
     * 
     * @return
     */
    public UIMenuItem menu() {
        MenuItem menu = new MenuItem();
        ui.getItems().add(menu);

        return new UIMenuItem(menu);
    }
}
