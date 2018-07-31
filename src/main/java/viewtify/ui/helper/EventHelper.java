/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.function.Consumer;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

import kiss.Signal;
import kiss.WiseBiConsumer;
import kiss.WiseTriConsumer;

/**
 * @version 2018/07/31 15:57:44
 */
public interface EventHelper<Self extends EventHelper> {

    Node ui();

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <E extends Event> Signal<E> when(EventType<E> actionType) {
        return new Signal<E>((observer, disposer) -> {
            EventHandler<E> listener = observer::accept;

            ui().addEventHandler(actionType, listener);

            return disposer.add(() -> {
                ui().removeEventHandler(actionType, listener);
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
    default <T extends Event> Self when(EventType<T> actionType, Runnable listener) {
        return when(actionType, e -> listener.run());
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event> Self when(EventType<T> actionType, Consumer<T> listener) {
        ui().addEventHandler(actionType, listener::accept);
        return (Self) this;
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event, A> Self when(EventType<T> actionType, Consumer<A> listener, A context) {
        return when(actionType, e -> listener.accept(context));
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event> Self when(EventType<T> actionType, WiseBiConsumer<T, Self> listener) {
        return when(actionType, e -> listener.accept(e, (Self) this));
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event, Context> Self when(EventType<T> actionType, Context context, WiseTriConsumer<T, Self, Context> listener) {
        return when(actionType, e -> listener.accept(e, (Self) this, context));
    }
}
