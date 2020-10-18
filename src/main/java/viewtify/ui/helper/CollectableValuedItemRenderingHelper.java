/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.function.Function;

import kiss.Signal;
import kiss.Variable;

public interface CollectableValuedItemRenderingHelper<Self extends CollectableValuedItemRenderingHelper, V>
        extends ValueHelper<Self, V>, CollectableItemRenderingHelper<Self, V> {

    /**
     * Set text for the current value.
     * 
     * @param text A text to set.
     * @return Chainable API.
     */
    default Self renderSelected(Function<V, String> text) {
        return renderSelectedWhen(s -> s.map(text::apply));
    }

    /**
     * Set text for the current value.
     * 
     * @param text A text to set.
     * @return Chainable API.
     */
    default Self renderSelectedByVariable(Function<V, Variable<String>> text) {
        return renderSelectedWhen(s -> s.flatVariable(text::apply));
    }

    /**
     * Set text for the current value.
     * 
     * @param text A text to set.
     * @return Chainable API.
     */
    Self renderSelectedWhen(Function<Signal<V>, Signal<String>> text);
}