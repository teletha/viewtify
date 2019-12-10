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

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public interface DisableHelper<Self extends DisableHelper> extends PropertyAccessHelper {

    /**
     * Enables itself.
     * 
     * @return Chainable API.
     */
    default Self enable() {
        property(Type.Disable).setValue(false);
        return (Self) this;
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
     * @return Chainable API.
     */
    default Self disable() {
        property(Type.Disable).setValue(true);
        return (Self) this;
    }

    /**
     * Gets whether it is disable.
     * 
     * @return A result.
     */
    default boolean isDisable() {
        return property(Type.Disable).getValue() == true;
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
            disable();
            I.schedule(time, unit, this::enable);
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
}
