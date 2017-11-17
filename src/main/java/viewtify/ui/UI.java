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

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Control;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import kiss.Signal;
import kiss.WiseBiConsumer;
import kiss.WiseTriConsumer;

/**
 * @version 2017/11/15 10:31:50
 */
public class UI<Self extends UI, W extends Node> {

    /** The actual view. */
    public final W ui;

    /** The validatiors. */
    private ValidationSupport validations;

    /**
     * @param ui
     */
    protected UI(W ui) {
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
}
