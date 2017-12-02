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

import javafx.scene.control.CheckBox;

import viewtify.View;

/**
 * @version 2017/11/15 9:54:15
 */
public class UICheckBox<T> extends UI<UICheckBox, CheckBox> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UICheckBox(CheckBox ui, View view) {
        super(ui, view);
    }

    /**
     * Set initial value.
     * 
     * @param initialValue
     * @return
     */
    public UICheckBox initial(boolean initialValue) {
        restore(ui.selectedProperty(), initialValue);
        return this;
    }

}
