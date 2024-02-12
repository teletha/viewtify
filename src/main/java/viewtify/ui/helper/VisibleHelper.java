/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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

/**
 * An interface providing methods for managing the visibility and opacity of a UI element.
 *
 * @param <Self> The type of the implementing class, enabling method chaining.
 */
public interface VisibleHelper<Self extends VisibleHelper> extends PropertyAccessHelper {

    /**
     * Manages the visibility and opacity of the UI element.
     *
     * @param state The visibility state.
     * @return The implementing class instance for method chaining.
     */
    default Self show(boolean state) {
        managed(state);
        return visible(state);
    }

    /**
     * Checks if the UI element is managed.
     *
     * @return True if managed, false otherwise.
     */
    default boolean isManaged() {
        return property(Type.Managed).getValue();
    }

    /**
     * Manages the UI element.
     *
     * @param state The management state.
     * @return The implementing class instance for method chaining.
     */
    default Self managed(boolean state) {
        property(Type.Managed).setValue(state);
        return (Self) this;
    }

    /**
     * Checks if the UI element is visible.
     *
     * @return True if visible, false otherwise.
     */
    default boolean isVisible() {
        return property(Type.Visible).getValue() == true;
    }

    /**
     * Sets the visibility of the UI element.
     *
     * @param state The visibility state.
     * @return The implementing class instance for method chaining.
     */
    default Self visible(boolean state) {
        property(Type.Visible).setValue(state);
        return (Self) this;
    }

    /**
     * Sets the visibility of the UI element based on a condition.
     *
     * @param context The context for the condition.
     * @param condition The condition to determine visibility.
     * @param <V> The type of the condition.
     * @return The implementing class instance for method chaining.
     */
    default <V> Self visibleWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            visibleWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Sets the visibility of the UI element based on a condition and additional conditions.
     *
     * @param condition The main condition.
     * @param conditions Additional conditions.
     * @return The implementing class instance for method chaining.
     */
    default Self visibleWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(property(Type.Visible)::setValue);
        }
        return (Self) this;
    }

    /**
     * Show itself when the specified condition is True, and hide it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self visibleWhen(Variable<Boolean> condition) {
        if (condition != null) {
            visibleWhen(Viewtify.property(condition));
        }
        return (Self) this;
    }

    /**
     * Show itself when the specified condition is True, and hide it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self visibleWhen(ObservableValue<Boolean> condition) {
        if (condition != null) {
            property(Type.Visible).bind(condition);
        }
        return (Self) this;
    }

    /**
     * Show itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self visibleDuring(long time, TimeUnit unit) {
        if (0 < time && unit != null) {
            visible(true);
            I.schedule(time, unit).to(() -> visible(false));
        }
        return (Self) this;
    }

    /**
     * Show itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self visibleDuring(long time, ChronoUnit unit) {
        if (0 < time && unit != null) {
            visibleDuring(time, TimeUnit.of(unit));
        }
        return (Self) this;
    }

    /**
     * Show itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self visibleBriefly() {
        return visibleDuring(400, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets whether it is invisible.
     * 
     * @return A result.
     */
    default boolean isInvisible() {
        return property(Type.Visible).getValue() == false;
    }

    /**
     * Hide itself.
     * 
     * @param state A invisible state.
     * @return Chainable API.
     */
    default Self invisible(boolean state) {
        return visible(!state);
    }

    /**
     * Hide itself when the specified condition is True, and show it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default <V> Self invisibleWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            invisibleWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Hide itself when the specified condition is True, and show it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self invisibleWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(VisibleHelper.this::invisible);
        }
        return (Self) this;
    }

    /**
     * Hide itself when the specified condition is True, and show it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self invisibleWhen(Variable<Boolean> condition) {
        if (condition != null) {
            invisibleWhen(Viewtify.property(condition));
        }
        return (Self) this;
    }

    /**
     * Hide itself when the specified condition is True, and show it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self invisibleWhen(ObservableValue<Boolean> condition) {
        if (condition != null) {
            visibleWhen(BooleanBinding.booleanExpression(condition).not());
        }
        return (Self) this;
    }

    /**
     * Hide itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self invisibleDuring(long time, TimeUnit unit) {
        if (0 < time && unit != null) {
            invisible(true);
            I.schedule(time, unit).to(() -> invisible(false));
        }
        return (Self) this;
    }

    /**
     * Hide itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self invisibleDuring(long time, ChronoUnit unit) {
        if (0 < time && unit != null) {
            invisibleDuring(time, TimeUnit.of(unit));
        }
        return (Self) this;
    }

    /**
     * Hide itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self invisibleBriefly() {
        return invisibleDuring(400, TimeUnit.MILLISECONDS);
    }

    /**
     * Set the opacity of this UI.
     * 
     * @param value
     * @return
     */
    default Self opacity(double value) {
        property(Type.Opacity).setValue(value);
        return (Self) this;
    }
}