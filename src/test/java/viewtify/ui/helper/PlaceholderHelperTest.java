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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import kiss.Variable;
import viewtify.JavaFXTester;
import viewtify.ui.Providers;
import viewtify.ui.UIComboBox;

public class PlaceholderHelperTest extends JavaFXTester {
    @Test
    void text1() {
        System.out.println("OK");
        UIComboBox ui = new UIComboBox(null);
        System.out.println("text1");
        ui.placeholder("TEST");
        assert ui.placeholderProperty().getValue().equals("TEST");
    }

    // @Test
    // void text2() {
    // UIText ui = new UIText(null, String.class);
    // System.out.println("text2");
    // ui.placeholder("TEST");
    // assert ui.placeholderProperty().getValue().equals("TEST");
    // }

    @Disabled
    @ParameterizedTest
    @ArgumentsSource(Providers.PlaceholderHelpers.class)
    void text(PlaceholderHelper<?> ui) {
        ui.placeholder("TEST");
        assert ui.placeholderProperty().getValue().equals("TEST");
    }

    @Disabled
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

    @Disabled
    @ParameterizedTest
    @ArgumentsSource(Providers.PlaceholderHelpers.class)
    void textVariableDiscadedByOtherVariable(PlaceholderHelper ui) {
        Variable text1 = Variable.of("TEST");
        ui.placeholder(text1);
        assert ui.placeholderProperty().getValue().equals("TEST");

        // sync from model
        text1.set("UPDATE");
        assert ui.placeholderProperty().getValue().equals("UPDATE");

        // change model
        Variable text2 = Variable.of("NEW");
        ui.placeholder(text2);
        assert ui.placeholderProperty().getValue().equals("NEW");

        // old model was discarded
        text1.set("FROM OLD");
        assert ui.placeholderProperty().getValue().equals("NEW");
    }

    @Disabled
    @ParameterizedTest
    @ArgumentsSource(Providers.PlaceholderHelpers.class)
    void textVariableDiscadedByText(PlaceholderHelper ui) {
        Variable text1 = Variable.of("TEST");
        ui.placeholder(text1);
        assert ui.placeholderProperty().getValue().equals("TEST");

        // sync from model
        text1.set("UPDATE");
        assert ui.placeholderProperty().getValue().equals("UPDATE");

        // change by text model
        ui.placeholder("NEW");
        assert ui.placeholderProperty().getValue().equals("NEW");

        // old model was discarded
        text1.set("FROM OLD");
        assert ui.placeholderProperty().getValue().equals("NEW");
    }
}
