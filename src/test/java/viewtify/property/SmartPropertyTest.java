/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.property;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;

import org.junit.jupiter.api.Test;

class SmartPropertyTest {

    @Test
    void get() {
        SmartProperty p = new SmartProperty();
        assert p.getValue() == null;

        p.setValue("ok");
        assert p.getValue() == "ok";
    }

    @Test
    void addInvalidationListener() {
        Observable[] value = new Observable[1];
        InvalidationListener listener = o -> {
            value[0] = o;
        };

        SmartProperty p = new SmartProperty();
        p.addListener(listener);

        assert value[0] == null;
        p.setValue("change");
        assert value[0] == p;
    }

    @Test
    void addSameInvalidationListener() {
        AtomicInteger count = new AtomicInteger();
        InvalidationListener listener = o -> {
            count.incrementAndGet();
        };

        SmartProperty p = new SmartProperty();
        p.addListener(listener);
        assert count.get() == 0;

        p.setValue("change");
        assert count.get() == 1;

        p.addListener(listener);
        p.setValue("update");
        assert count.get() == 2;
    }

    @Test
    void addMultipleInvalidationListeners() {
        AtomicInteger count = new AtomicInteger();
        InvalidationListener listener1 = o -> {
            count.incrementAndGet();
        };
        InvalidationListener listener2 = o -> {
            count.incrementAndGet();
        };

        SmartProperty p = new SmartProperty();
        p.addListener(listener1);
        p.addListener(listener2);
        assert count.get() == 0;

        p.setValue("change");
        assert count.get() == 2;
    }

    @Test
    void removeInvalidationListener() {
        AtomicInteger count = new AtomicInteger();
        InvalidationListener listener = o -> {
            count.incrementAndGet();
        };

        SmartProperty p = new SmartProperty();
        p.addListener(listener);
        assert count.get() == 0;

        p.setValue("change");
        assert count.get() == 1;

        p.removeListener(listener);
        p.setValue("update");
        assert count.get() == 1;
    }

    @Test
    void binding() {
        SmartProperty<String> source = new SmartProperty();
        SimpleStringProperty other = new SimpleStringProperty();
        source.bind(other);
        assert source.getValue() == null;

        other.set("change from other");
        assert source.getValue() == "change from other";

        source.setValue("no effect from source");
        assert other.getValue() == "change from other";

        source.unbind();
        other.set("no effect from other");
        assert source.getValue() == "no effect from source";
    }

    @Test
    void bindignBidrectional() {
        SmartProperty<String> source = new SmartProperty();
        SimpleStringProperty other = new SimpleStringProperty();
        source.bindBidirectional(other);
        assert source.getValue() == null;

        other.set("change from other");
        assert source.getValue() == "change from other";

        source.setValue("change from source");
        assert other.getValue() == "change from source";

        source.unbindBidirectional(other);
        other.set("no effect from other");
        assert source.getValue() == "change from source";

        source.setValue("no effect from source");
        assert other.getValue() == "no effect from other";
    }
}
