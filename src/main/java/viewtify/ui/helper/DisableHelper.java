/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import kiss.Variable;
import viewtify.Viewtify;

/**
 * @version 2017/12/04 9:11:19
 */
public interface DisableHelper<Self extends DisableHelper> {

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
        return disableWhen(Viewtify.calculate(condition));
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

    /**
     * Validation helper.
     */
    default Self enableWhen(ObservableValue<? extends Boolean> condition) {
        return disableWhen(Viewtify.calculate(condition, v -> !v));
    }
}
