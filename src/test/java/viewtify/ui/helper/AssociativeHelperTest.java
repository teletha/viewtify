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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import viewtify.JavaFXTester;
import viewtify.ui.Providers;

class AssociativeHelperTest extends JavaFXTester {

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void get(AssociativeHelper ui) {
        assert ui.get(String.class).isAbsent();

        ui.set(String.class, "TEST");
        assert ui.get(String.class).is("TEST");
        assert ui.get(Class.class).isAbsent();
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void getNullKey(AssociativeHelper ui) {
        assert ui.get(null).isAbsent();
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void getWithName(AssociativeHelper ui) {
        ui.set(String.class, "1", "value1");
        ui.set(String.class, "2", "value2");

        assert ui.get(String.class, "1").is("value1");
        assert ui.get(String.class, "2").is("value2");
        assert ui.get(String.class, "3").isAbsent();
        assert ui.get(Class.class, "1").isAbsent();
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void getWithNullName(AssociativeHelper ui) {
        ui.set(String.class, "1", "value1");
        ui.set(String.class, null, "valueNull");

        assert ui.get(String.class, "1").is("value1");
        assert ui.get(String.class, "2").isAbsent();
        assert ui.get(String.class).is("valueNull");
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void exact(AssociativeHelper ui) {
        assert ui.exact(Map.class) instanceof Map;
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void exactNullKey(AssociativeHelper ui) {
        assert ui.exact(null) == null;
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void set(AssociativeHelper ui) {
        ui.set(String.class, "TEST");
        assert ui.get(String.class).is("TEST");
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void setNullKey(AssociativeHelper ui) {
        ui.set(null, "TEST");
        assert ui.get(null).isAbsent();
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void delete(AssociativeHelper ui) {
        ui.delete(String.class);
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.AssociativeHelpers.class)
    void deleteNullKey(AssociativeHelper ui) {
        ui.set(null, "TEST");
        assert ui.get(null).isAbsent();
    }
}
