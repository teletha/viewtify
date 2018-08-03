/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.beans.property.BooleanProperty;

import viewtify.ui.UserInterface;

/**
 * @version 2018/08/03 19:15:04
 */
public interface EditableHelper<Self extends EditableHelper> {

    /**
     * Select editable property.
     * 
     * @return
     */
    BooleanProperty editable();

    /**
     * Change this {@link UserInterface}'s editable state.
     * 
     * @return Chainable API.
     */
    default Self editable(boolean editable) {
        editable().setValue(editable);
        return (Self) this;
    }

    /**
     * Make this {@link UserInterface} uneditable.
     * 
     * @return Chainable API.
     */
    default Self uneditable() {
        editable().setValue(false);
        return (Self) this;
    }
}
