/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

import org.junit.jupiter.api.Test;

/**
 * @version 2018/09/11 9:48:31
 */
class DelegationPropertyTest {

    Property<String> base = new SimpleStringProperty();

    DelegatingProperty<String, Integer> wrap = new DelegatingProperty<>(base, Integer::parseInt, String::valueOf);

    @Test
    void getValue() {
        assert wrap.getValue() == null;
        base.setValue("11");
        assert wrap.getValue() == 11;
    }

    @Test
    void setValue() {
        wrap.setValue(5);
        assert base.getValue().equals("5");
        wrap.setValue(null);
        assert base.getValue() == null;
    }

    @Test
    void invalidationListener() {
        AtomicReference change = new AtomicReference();
        InvalidationListener listener = o -> {
            change.set(o);
        };

        wrap.addListener(listener);
        wrap.setValue(9);
        assert change.getAndSet(null) == wrap;
        assert wrap.getValue() == 9; // reset invalidation

        wrap.setValue(null);
        assert change.getAndSet(null) == wrap;
        assert wrap.getValue() == null; // reset invalidation

        // remove
        wrap.removeListener(listener);
        wrap.setValue(2);
        assert change.getAndSet(null) == null;
        assert wrap.getValue() == 2; // reset invalidation
    }

    @Test
    void changeListener() {
        AtomicReference observable = new AtomicReference();
        AtomicReference<Integer> oldValue = new AtomicReference();
        AtomicReference<Integer> newValue = new AtomicReference();
        ChangeListener<Integer> listener = (obj, o, n) -> {
            observable.set(obj);
            oldValue.set(o);
            newValue.set(n);
        };

        wrap.addListener(listener);
        wrap.setValue(9);
        assert observable.getAndSet(null) == wrap;
        assert oldValue.getAndSet(null) == null;
        assert newValue.getAndSet(null) == 9;

        wrap.setValue(10);
        assert observable.getAndSet(null) == wrap;
        assert oldValue.getAndSet(null) == 9;
        assert newValue.getAndSet(null) == 10;

        wrap.setValue(null);
        assert observable.getAndSet(null) == wrap;
        assert oldValue.getAndSet(null) == 10;
        assert newValue.getAndSet(null) == null;

        // remove
        wrap.removeListener(listener);
        wrap.setValue(6);
        assert observable.getAndSet(null) == null;
        assert oldValue.getAndSet(null) == null;
        assert newValue.getAndSet(null) == null;
    }
}
