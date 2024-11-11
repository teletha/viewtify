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

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import kiss.Variable;
import viewtify.JavaFXTester;
import viewtify.ui.HelperProvider;

public class DisableHelperTest extends JavaFXTester {

    private final Helper ui = new Helper();

    @ParameterizedTest
    @ArgumentsSource(HelperProvider.class)
    void disable(DisableHelper ui) {
        assert ui.isEnable();

        ui.disable(true);
        assert ui.isDisable();
        assert ui.isEnable() == false;
    }

    @Test
    void disableNow() {
        ui.disableNow();
        assert ui.isDisable();
        assert ui.isEnable() == false;
    }

    @Test
    void disableDuration() throws InterruptedException {
        ui.disableDuring(10, TimeUnit.MILLISECONDS);
        assert ui.isDisable();

        Thread.sleep(15);
        assert ui.isEnable();

        ui.disableDuring(10, ChronoUnit.MILLIS);
        assert ui.isDisable();

        Thread.sleep(15);
        assert ui.isEnable();
    }

    @Test
    void disableWhenVariable() {
        Variable<Boolean> condition = Variable.of(true);
        ui.disableWhen(condition);
        assert ui.isDisable();

        condition.set(false);
        assert ui.isEnable();
    }

    @Test
    void disableWhenVariables() {
        Variable<Boolean> condition1 = Variable.of(true);
        ui.disableWhen(condition1);
        Variable<Boolean> condition2 = Variable.of(true);
        ui.disableWhen(condition2);
        assert ui.isDisable();

        // from old model
        condition1.set(false);
        assert ui.isDisable();

        // form new model
        condition2.set(false);
        assert ui.isEnable();
    }

    @Test
    void disableWhenProperty() {
        SimpleBooleanProperty condition = new SimpleBooleanProperty(true);
        ui.disableWhen(condition);
        assert ui.isDisable();

        condition.set(false);
        assert ui.isEnable();
    }

    @Test
    void disableWhenProperties() {
        SimpleBooleanProperty condition1 = new SimpleBooleanProperty(true);
        ui.disableWhen(condition1);
        SimpleBooleanProperty condition2 = new SimpleBooleanProperty(true);
        ui.disableWhen(condition2);
        assert ui.isDisable();

        // from old model
        condition1.set(false);
        assert ui.isDisable();

        // form new model
        condition2.set(false);
        assert ui.isEnable();
    }

    @Test
    void enableWhenVariable() {
        Variable<Boolean> condition = Variable.of(true);
        ui.enableWhen(condition);
        assert ui.isEnable();

        condition.set(false);
        assert ui.isDisable();
    }

    @Test
    void enableWhenVariables() {
        Variable<Boolean> condition1 = Variable.of(true);
        ui.enableWhen(condition1);
        Variable<Boolean> condition2 = Variable.of(true);
        ui.enableWhen(condition2);
        assert ui.isEnable();

        // from old model
        condition1.set(false);
        assert ui.isEnable();

        // form new model
        condition2.set(false);
        assert ui.isDisable();
    }

    @Test
    void enableWhenVariableCondition() {
        Variable<String> condition = Variable.of("");
        ui.enableWhen(condition, x -> x.isEmpty());
        assert ui.isEnable();

        condition.set("OK");
        assert ui.isDisable();
    }

    // @Test
    // void enableWhenVariableConditions() {
    // Variable<String> condition1 = Variable.of("");
    // ui.enableWhen(condition1, x -> x.isEmpty());
    // Variable<String> condition2 = Variable.of("");
    // ui.enableWhen(condition2, x -> x.isEmpty());
    // assert ui.isEnable();
    //
    // // from old model
    // condition1.set("FAIL");
    // assert ui.isEnable();
    //
    // // form new model
    // condition2.set("OK");
    // assert ui.isDisable();
    // }

    @Test
    void enableWhenProperty() {
        SimpleBooleanProperty condition = new SimpleBooleanProperty(true);
        ui.enableWhen(condition);
        assert ui.isEnable();

        condition.set(false);
        assert ui.isDisable();
    }

    @Test
    void enableWhenProperties() {
        SimpleBooleanProperty condition1 = new SimpleBooleanProperty(true);
        ui.enableWhen(condition1);
        SimpleBooleanProperty condition2 = new SimpleBooleanProperty(true);
        ui.enableWhen(condition2);
        assert ui.isEnable();

        // from old model
        condition1.set(false);
        assert ui.isEnable();

        // form new model
        condition2.set(false);
        assert ui.isDisable();
    }

    private static class Helper implements DisableHelper<Helper> {

        private final BooleanProperty disable = new SimpleBooleanProperty();

        /**
         * {@inheritDoc}
         */
        @Override
        public Property<Boolean> disableProperty() {
            return disable;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object ui() {
            return this;
        }
    }
}
