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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import kiss.Variable;
import viewtify.JavaFXTester;
import viewtify.ui.Providers;

public class PlaceholderHelperTest extends JavaFXTester {

    @ParameterizedTest
    @ArgumentsSource(Providers.PlaceholderHelpers.class)
    void text(PlaceholderHelper<?> ui) {
        ui.placeholder("TEST");
        assert ui.placeholderProperty().getValue().equals("TEST");
    }

    @ParameterizedTest
    @ArgumentsSource(Providers.PlaceholderHelpers.class)
    void variable(PlaceholderHelper<?> ui) {
        Variable<String> text = Variable.of("TEST");

        ui.placeholder(text);
        assert ui.placeholderProperty().getValue().equals("TEST");

        // update from model
        text.set("UPDATE");
        assert ui.placeholderProperty().getValue().equals("UPDATE");
    }
}
