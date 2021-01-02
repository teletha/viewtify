/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.Label;

import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;

public interface PlaceholderHelper<Self extends PlaceholderHelper> extends PropertyAccessHelper {

    /**
     * Set placeholder text.
     * 
     * @param text A text to set.
     * @return Chainable API.
     */
    default Self placeholder(Object text) {
        return placeholder(new Label(String.valueOf(text)));
    }

    /**
     * Set placeholder text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self placeholder(Variable text) {
        text.observing().on(Viewtify.UIThread).to((Consumer) this::placeholder);
        return (Self) this;
    }

    /**
     * Set placeholder text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self placeholder(Property text) {
        property(Type.Text).bindBidirectional(text);
        return (Self) this;
    }

    /**
     * Set the specified {@link Node} as literal component.
     * 
     * @param text A literal component to set.
     * @return Chainable API.
     */
    default Self placeholder(UserInterfaceProvider text) {
        return placeholder(text.ui().getStyleableNode());
    }

    /**
     * Set the specified {@link Node} component.
     * 
     * @param node A node component to set.
     * @return Chainable API.
     */
    default Self placeholder(Node node) {
        property(Type.Placeholder).setValue(node);
        return (Self) this;
    }
}