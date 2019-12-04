/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.beans.value.ObservableValue;

import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

/**
 * @version 2018/09/11 16:46:22
 */
public interface DisableHelper<Self extends DisableHelper> extends PropertyHelper {

    /**
     * Validation helper.
     */
    default Self disableWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        condition.combineLatest(conditions, (one, other) -> one || other).to(property(Type.Disable)::setValue);

        return (Self) this;
    }

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
            property(Type.Disable).bind(condition);
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
