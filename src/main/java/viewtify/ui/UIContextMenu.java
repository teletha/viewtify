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

import javafx.scene.control.CheckMenuItem;
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
