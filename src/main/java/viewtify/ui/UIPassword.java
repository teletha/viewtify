/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.PasswordField;

import viewtify.View;

/**
 * @version 2018/08/27 23:24:41
 */
public class UIPassword extends AbstractTextField<UIPassword, PasswordField> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIPassword(PasswordField ui, View view) {
        super(ui, view);
    }
}
