/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Control;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseBiConsumer;
import kiss.WiseTriConsumer;

/**
 * @version 2017/11/15 10:31:50
 */
public class UI<Self extends UI, W extends Node> {

    /** User configuration for UI. */
    private static final Preference preference = I.make(Preference.class).restore();

    /** The actual view. */
    public final W ui;

    /** The validatiors. */
    private ValidationSupport validations;

    /** Do restore oonly once. */
    private boolean restored = false;

    /**
     * @param ui
     */
    public UI(W ui) {
        this.ui = ui;
    }

    /**
     * Select parent {@link Node}.
     * 
     * @return
     */
    public UI parent() {
        return new UI(ui.getParent());
    }

    /**
     * Apply single state class by the specified enum.
     * 
     * @param node
     * @param state
     */
    public <E extends Enum<E>> Self style(E state) {
        if (state != null) {
            ObservableList<String> classes = ui.getStyleClass();

            for (Enum value : state.getClass().getEnumConstants()) {
                String name = value.name();

                if (state == value) {
                    if (!classes.contains(name)) {
                        classes.add(name);
                    }
                } else {
                    classes.remove(name);
                }
            }
        }
        return (Self) this;
    }

    /**
     * Apply single state class by the specified enum.
     * 
     * @param node
     * @param state
     */
    public <E extends Enum<E>> Self style(Variable<E> state) {
        return style(state.get());
    }

    /**
     * Clear all style for the specified enum type.
     * 
     * @param class1
     */
    public <E extends Enum<E>> Self unstyle(Class<E> style) {
        if (style != null) {
            ObservableList<String> classes = ui.getStyleClass();

            for (Enum value : style.getEnumConstants()) {
                classes.remove(value.name());
            }
        }
        return (Self) this;
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <E extends Event> Signal<E> when(EventType<E> actionType) {
        return new Signal<E>((observer, disposer) -> {
            EventHandler<E> listener = observer::accept;

            ui.addEventHandler(actionType, listener);

            return disposer.add(() -> {
                ui.removeEventHandler(actionType, listener);
            });
        });
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event> Self when(EventType<T> actionType, Runnable listener) {
        return when(actionType, e -> listener.run());
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event> Self when(EventType<T> actionType, EventHandler<T> listener) {
        ui.addEventHandler(actionType, listener);
        return (Self) this;
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event, A> Self when(EventType<T> actionType, Consumer<A> listener, A context) {
        return when(actionType, e -> listener.accept(context));
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event> Self when(EventType<T> actionType, WiseBiConsumer<T, Self> listener) {
        return when(actionType, e -> listener.accept(e, (Self) this));
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event, Context> Self when(EventType<T> actionType, Context context, WiseTriConsumer<T, Self, Context> listener) {
        return when(actionType, e -> listener.accept(e, (Self) this, context));
    }

    /**
     * Validation helper.
     * 
     * @param condition
     * @return
     */
    public Self require(Predicate<Self> condition) {
        if (ui instanceof Control) {
            if (validations == null) {
                validations = new ValidationSupport();
            }

            validations.registerValidator((Control) ui, false, Validator.createPredicateValidator(v -> {
                return condition.test((Self) this);
            }, ""));
        }
        return (Self) this;
    }

    /**
     * Validation helper.
     */
    public ReadOnlyBooleanProperty isInvalid() {
        if (validations == null) {
            validations = new ValidationSupport();
        }
        return validations.invalidProperty();
    }

    /**
     * Validation helper.
     */
    public Self disableWhen(ObservableValue<? extends Boolean> condition) {
        if (condition != null) {
            ui.disableProperty().bind(condition);
        }
        return (Self) this;
    }

    protected final <T> void restore(Property<T> property, T value) {
        if (value == null || restored) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
        restored = true;

        String id = ui.getId();

        property.addListener((p, o, n) -> {
            preference.put(id, I.transform(n, String.class));
            preference.store();
        });

        if (id != null) {
            String stored = preference.get(id);

            if (stored != null) {
                value = (T) I.transform(stored, value.getClass());
            }
        }
        property.setValue(value);
    }
}
