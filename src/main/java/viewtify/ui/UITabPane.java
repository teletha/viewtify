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

import javafx.scene.control.TabPane;

import viewtify.Viewty;

/**
 * @version 2017/11/29 10:12:34
 */
public class UITabPane<T> extends UI<UITabPane, TabPane> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITabPane(TabPane ui, Viewty view) {
        super(ui, view);
    }

    /**
     * Set initial selected index.
     * 
     * @param initialSelectedIndex
     * @return
     */
    public UITabPane initial(int initialSelectedIndex) {
        restore(ui.getSelectionModel().selectedIndexProperty(), v -> ui.getSelectionModel().select((int) v), initialSelectedIndex);
        return this;
    }
}
