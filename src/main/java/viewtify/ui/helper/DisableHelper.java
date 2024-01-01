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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;

import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseConsumer;
import kiss.WiseFunction;
import viewtify.Viewtify;
import viewtify.util.FXUtils;

public interface DisableHelper<Self extends DisableHelper> extends PropertyAccessHelper {

    /**
     * Get the disable property.
     * 
     * @return
     */
    default Property<Boolean> disableProperty() {
        return property(Type.Disable);
    }

    /**
     * Gets whether it is disable.
     * 
     * @return A result.
     */
    default boolean isDisable() {
        return disableProperty().getValue() == true;
    }

    /**
     * Disables itself.
     * 
     * @return Chainable API.
     */
    default Self disableNow() {
        return disable(true);
    }

    /**
     * Disables itself.
     * 
     * @param state A disable state.
     * @return Chainable API.
     */
    default Self disable(boolean state) {
        disableProperty().setValue(state);
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
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
     * @param conditions A list of timing conditions.
     * @return Chainable API.
     */
    default Self disableWhen(List<Signal<Boolean>> conditions) {
        if (conditions != null) {
            switch (conditions.size()) {
            case 0:
                break;

            case 1:
                disableWhen(conditions.get(0));
                break;

            default:
                disableWhen(conditions.get(0), conditions.subList(1, conditions.size()).toArray(Signal[]::new));
                break;
            }
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
            condition.combineLatest(conditions, (one, other) -> one || other).to(disableProperty()::setValue);
        }
        return (Self) this;
    }

    /**
     * Disables itself when any specified condition is True, and enables it when False.
     * 
     * @param conditions A list of timing conditions.
     * @return Chainable API.
     */
    default Self disableWhenAny(List<Signal<Boolean>> conditions) {
        if (conditions != null) {
            switch (conditions.size()) {
            case 0:
                break;

            case 1:
                disableWhenAny(conditions.get(0));
                break;

            default:
                disableWhenAny(conditions.get(0), conditions.subList(1, conditions.size()).toArray(Signal[]::new));
                break;
            }
        }
        return (Self) this;
    }

    /**
     * Disables itself when any specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self disableWhenAny(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one && other).to(disableProperty()::setValue);
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
            disableProperty().bind(condition);
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
        return disableProperty().getValue() == false;
    }

    /**
     * Enables itself.
     * 
     * @return Chainable API.
     */
    default Self enableNow() {
        return enable(true);
    }

    /**
     * Enables itself.
     * 
     * @param state A disable state.
     * @return Chainable API.
     */
    default Self enable(boolean state) {
        return disable(!state);
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default <V> Self enableWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            enableWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param conditions A list of timing conditions.
     * @return Chainable API.
     */
    default Self enableWhen(List<Signal<Boolean>> conditions) {
        if (conditions != null) {
            switch (conditions.size()) {
            case 0:
                break;

            case 1:
                enableWhen(conditions.get(0));
                break;

            default:
                enableWhen(conditions.get(0), conditions.subList(1, conditions.size()).toArray(Signal[]::new));
                break;
            }
        }
        return (Self) this;
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self enableWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(this::enable);
        }
        return (Self) this;
    }

    /**
     * Enables itself when any specified condition is True, and enables it when False.
     * 
     * @param conditions A list of timing conditions.
     * @return Chainable API.
     */
    default Self enableWhenAny(List<Signal<Boolean>> conditions) {
        if (conditions != null) {
            switch (conditions.size()) {
            case 0:
                break;

            case 1:
                enableWhenAny(conditions.get(0));
                break;

            default:
                enableWhenAny(conditions.get(0), conditions.subList(1, conditions.size()).toArray(Signal[]::new));
                break;
            }
        }
        return (Self) this;
    }

    /**
     * Enables itself when any specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self enableWhenAny(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one && other).to(this::enable);
        }
        return (Self) this;
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
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
     * Enables itself when the specified condition is True, and enables it when False.
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
     * Enables itself for a specified time.
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
     * Enables itself for a specified time.
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
     * Enables itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self enableBriefly() {
        return enableDuring(400, TimeUnit.MILLISECONDS);
    }

    /**
     * Show progress indicator.
     * 
     * @return
     */
    default Self showLoader() {
        Region pane = (Region) ui();
        FXUtils.ensureAssociation(pane, LoaderEffect.class).show(pane);

        return disableNow();
    }

    /**
     * Hide progress indicator.
     * 
     * @return
     */
    default Self hideLoader() {
        Region pane = (Region) ui();
        FXUtils.getAssociation(pane, LoaderEffect.class).to(LoaderEffect::hide);

        return enableNow();
    }

    /**
     * Enable progress indicator with {@link Signal#effectOnLifecycle(WiseFunction)}.
     * 
     * @param <T>
     * @return
     */
    default <T> WiseFunction<Disposable, WiseConsumer<T>> enableLoader() {
        return disposer -> {
            showLoader();
            disposer.add(this::hideLoader);
            return null;
        };
    }
}