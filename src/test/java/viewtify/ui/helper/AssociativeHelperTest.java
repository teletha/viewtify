/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class AssociativeHelperTest {

    private AssociativeHelper ui = AssociativeHelper.of(this);

    @Test
    void get() {
        assert ui.get(String.class).isAbsent();

        ui.set(String.class, "TEST");
        assert ui.get(String.class).is("TEST");
        assert ui.get(Class.class).isAbsent();
    }

    @Test
    void getNullKey() {
        assert ui.get(null).isAbsent();
    }

    @Test
    void getWithName() {
        ui.set(String.class, "1", "value1");
        ui.set(String.class, "2", "value2");

        assert ui.get(String.class, "1").is("value1");
        assert ui.get(String.class, "2").is("value2");
        assert ui.get(String.class, "3").isAbsent();
        assert ui.get(Class.class, "1").isAbsent();
    }

    @Test
    void getWithNullName() {
        ui.set(String.class, "1", "value1");
        ui.set(String.class, null, "valueNull");

        assert ui.get(String.class, "1").is("value1");
        assert ui.get(String.class, "2").isAbsent();
        assert ui.get(String.class).is("valueNull");
    }

    @Test
    void exact() {
        assert ui.exact(Map.class) instanceof Map;
    }

    @Test
    void exactNullKey() {
        assert ui.exact(null) == null;
    }

    @Test
    void set() {
        ui.set(String.class, "TEST");
        assert ui.get(String.class).is("TEST");
    }

    @Test
    void setNullKey() {
        ui.set(null, "TEST");
        assert ui.get(null).isAbsent();
    }

    @Test
    void delete() {
        ui.dispose(String.class);
    }

    @Test
    void deleteNullKey() {
        ui.set(null, "TEST");
        assert ui.get(null).isAbsent();
    }
}
