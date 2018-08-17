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

import javafx.beans.property.Property;
import javafx.scene.control.TextField;

import viewtify.View;
import viewtify.ui.helper.PreferenceHelper;

/**
 * @version 2018/08/06 19:41:38
 */
public class UIText extends UserInterface<UIText, TextField> implements PreferenceHelper<UIText, String> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIText(TextField ui, View view) {
        super(ui, view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<String> model() {
        return ui.textProperty();
    }
}
