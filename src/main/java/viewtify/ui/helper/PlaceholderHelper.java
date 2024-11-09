/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.beans.property.StringProperty;

import kiss.I;
import kiss.Variable;
import viewtify.Viewtify;

public interface PlaceholderHelper<Self extends PlaceholderHelper> extends PropertyAccessHelper {

    /**
     * Get the placeholder property.
     * 
     * @return
     */
    StringProperty placeholderProperty();

    /**
     * Set placeholder text.
     * 
     * @param text A text to set.
     * @return Chainable API.
     */
    default Self placeholder(Object text) {
        return placeholder(Variable.of(text));
    }

    /**
     * Set placeholder text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self placeholder(Variable text) {
        text.observing().on(Viewtify.UIThread).to(v -> {
            placeholderProperty().set(I.transform(text, String.class));
        });
        return (Self) this;
    }
}