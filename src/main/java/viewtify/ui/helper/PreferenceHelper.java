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

import javafx.beans.property.Property;

import kiss.Variable;

/**
 * @version 2017/12/28 14:11:06
 */
public interface PreferenceHelper<Self extends PreferenceHelper, V> {

    Property<V> preference();

    /**
     * This preference synchronizes with the specified value.
     */
    default Self model(Property<V> value) {
        if (value != null) {
            Property<V> pref = preference();
            pref.unbind();
            pref.setValue(value.getValue());
            pref.bind(value);
        }
        return (Self) this;
    }

    /**
     * This preference synchronizes with the specified value.
     */
    default Self model(Variable<V> value) {
        if (value != null) {
            Property<V> pref = preference();
            pref.setValue(value.get());
            preference().addListener((source, oldValue, newValue) -> {
                value.set(newValue);
            });
        }
        return (Self) this;
    }
}
