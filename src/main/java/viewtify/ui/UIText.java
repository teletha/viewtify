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

import java.util.Objects;

import javafx.scene.control.TextField;

import kiss.I;
import kiss.Signal;
import viewtify.View;
import viewtify.Viewtify;

/**
 * @version 2017/11/15 9:54:15
 */
public class UIText extends UserInterface<UIText, TextField> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIText(TextField ui, View view) {
        super(ui, view);
    }

    /**
     * Get text.
     * 
     * @param text
     */
    public String text() {
        return ui.getText();
    }

    /**
     * Set text.
     * 
     * @param text
     */
    public UIText text(Object text) {
        ui.setText(Objects.toString(text));
        return this;
    }

    /**
     * Set initial value.
     * 
     * @param initialValue
     * @return
     */
    public UIText initial(String initialValue) {
        restore(ui.textProperty(), initialValue);
        return this;
    }

    /**
     * Get text as the specified type.
     * 
     * @return
     */
    public <T> T valueOr(T defaultValue) {
        try {
            return I.transform(text(), (Class<T>) defaultValue.getClass());
        } catch (Throwable e) {
            return defaultValue;

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Signal<?> validateWhen() {
        return Viewtify.signal(ui.textProperty());
    }
}
