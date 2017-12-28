/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.property.Property;

import kiss.Variable;

/**
 * @version 2017/12/28 14:11:06
 */
public interface PreferenceHelper<Self extends PreferenceHelper, V> {

    Property<V> preference();

    default Self model(Variable<V> property) {
        return model(property::get, property::set);
    }

    default Self model(Supplier<V> getter, Consumer<V> setter) {
        preference().addListener((source, oldValue, newValue) -> {
            setter.accept(newValue);
        });
        preference().setValue(getter.get());

        return (Self) this;
    }
}
