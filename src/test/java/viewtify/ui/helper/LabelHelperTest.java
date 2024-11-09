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

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import kiss.Variable;
import viewtify.JavaFXTester;
import viewtify.ui.HelperProvider;

public class LabelHelperTest extends JavaFXTester {

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void text(LabelHelper ui) {
        ui.text("TEST");
        assert ui.text().equals("TEST");

        ui.text(10);
        assert ui.text().equals("10");
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void textNull(LabelHelper ui) {
        ui.text((String) null);
        assert ui.text() == null;
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void textVariable(LabelHelper ui) {
        Variable text = Variable.of("TEST");
        ui.text(text);
        assert ui.text().equals("TEST");

        // sync from model
        text.set("UPDATE");
        assert ui.text().equals("UPDATE");

        text.set(10);
        assert ui.text().equals("10");

        // disconnect
        ui.text((Variable) null);
        text.set("STOP");
        assert ui.text().equals("10");
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void textVariableDiscadedByOtherVariable(LabelHelper ui) {
        Variable text1 = Variable.of("TEST");
        ui.text(text1);
        assert ui.text().equals("TEST");

        // sync from model
        text1.set("UPDATE");
        assert ui.text().equals("UPDATE");

        // change model
        Variable text2 = Variable.of("NEW");
        ui.text(text2);
        assert ui.text().equals("NEW");

        // old model was discarded
        text1.set("FROM OLD");
        assert ui.text().equals("NEW");
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void textVariableDiscadedByText(LabelHelper ui) {
        Variable text = Variable.of("TEST");
        ui.text(text);
        assert ui.text().equals("TEST");

        // sync from model
        text.set("UPDATE");
        assert ui.text().equals("UPDATE");

        // update by text model
        ui.text("NEW");
        assert ui.text().equals("NEW");

        // old model was discarded
        text.set("FROM OLD");
        assert ui.text().equals("NEW");
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void textNode(LabelHelper<?> ui) {
        Label text = new Label("TEST");
        ui.text(text);
        assert ui.text() == null;
        assert ui.graphic() == text;
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void color(LabelHelper<?> ui) {
        ui.color(Color.RED);
        assert ui.color().equals(Color.RED);
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void fontSize(LabelHelper<?> ui) {
        ui.font(10);
        assert ui.font().getSize() == 10;
    }

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void fontName(LabelHelper<?> ui) {
        ui.font("System");
        assert ui.font().getFamily().equals("System");
    }
}
