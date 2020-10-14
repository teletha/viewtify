/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public interface DisableHelper<Self extends DisableHelper> extends PropertyAccessHelper {

    /**
     * Gets whether it is disable.
     * 
     * @return A result.
     */
    default boolean isDisable() {
        return property(Type.Disable).getValue() == true;
    }

    /**
     * Disables itself.
     * 
     * @param state A disable state.
     * @return Chainable API.
     */
    default Self disable(boolean state) {
        property(Type.Disable).setValue(state);
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default <V> Self disableWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            disableWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self disableWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(property(Type.Disable)::setValue);
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self disableWhen(Variable<Boolean> condition) {
        if (condition != null) {
            disableWhen(Viewtify.property(condition));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param target A target value to test.
     * @param condition A condition.
     * @return Chainable API.
     */
    default <V> Self disableWhen(Variable<V> target, Predicate<V> condition) {
        if (target != null && condition != null) {
            disableWhen(target.observing().is(condition));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self disableWhen(ObservableValue<Boolean> condition) {
        if (condition != null) {
            property(Type.Disable).bind(condition);
        }
        return (Self) this;
    }

    /**
     * Disable itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self disableDuring(long time, TimeUnit unit) {
        if (0 < time && unit != null) {
            disable(true);
            I.schedule(time, unit).to(() -> disable(false));
        }
        return (Self) this;
    }

    /**
     * Disable itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self disableDuring(long time, ChronoUnit unit) {
        if (0 < time && unit != null) {
            disableDuring(time, TimeUnit.of(unit));
        }
        return (Self) this;
    }

    /**
     * Disable itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self disableBriefly() {
        return disableDuring(400, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets whether it is enable.
     * 
     * @return A result.
     */
    default boolean isEnable() {
        return property(Type.Disable).getValue() == false;
    }

    /**
     * Disables itself.
     * 
     * @param state A disable state.
     * @return Chainable API.
     */
    default Self enable(boolean state) {
        return disable(!state);
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default <V> Self enableWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            enableWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self enableWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(DisableHelper.this::enable);
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self enableWhen(Variable<Boolean> condition) {
        if (condition != null) {
            enableWhen(Viewtify.property(condition));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self enableWhen(ObservableValue<Boolean> condition) {
        if (condition != null) {
            disableWhen(BooleanBinding.booleanExpression(condition).not());
        }
        return (Self) this;
    }

    /**
     * Disable itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self enableDuring(long time, TimeUnit unit) {
        if (0 < time && unit != null) {
            enable(true);
            I.schedule(time, unit).to(() -> enable(false));
        }
        return (Self) this;
    }

    /**
     * Disable itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self enableDuring(long time, ChronoUnit unit) {
        if (0 < time && unit != null) {
            enableDuring(time, TimeUnit.of(unit));
        }
        return (Self) this;
    }

    /**
     * Disable itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self enableBriefly() {
        return enableDuring(400, TimeUnit.MILLISECONDS);
    }
}