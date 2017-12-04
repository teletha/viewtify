/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.functionality;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import kiss.Variable;
import viewtify.calculation.VariableBinding;

/**
 * @version 2017/12/04 9:11:19
 */
public interface Disable<Self extends Disable> {

    /**
     * Select disable property.
     * 
     * @return
     */
    Property<Boolean> disable();

    /**
     * Validation helper.
     */
    default Self disableWhen(Variable<? extends Boolean> condition) {
        return disableWhen(new VariableBinding<>(condition));
    }

    /**
     * Validation helper.
     */
    default Self disableWhen(ObservableValue<? extends Boolean> condition) {
        if (condition != null) {
            disable().bind(condition);
        }
        return (Self) this;
    }
}
