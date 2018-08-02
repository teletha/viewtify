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
import javafx.scene.Node;

import kiss.Signal;
import kiss.WiseBiConsumer;
import kiss.WiseTriConsumer;

/**
 * @version 2018/08/01 21:17:18
 */
public interface UserActionHelper<Self extends UserActionHelper> {

    Node ui();

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <E extends Event> Signal<E> when(User<E> actionType) {
        return actionType.hook.apply(new Signal<E>((observer, disposer) -> {
            EventHandler<E> listener = observer::accept;

            ui().addEventHandler(actionType.type, listener);

            return disposer.add(() -> {
                ui().removeEventHandler(actionType.type, listener);
            });
        }), this);
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event> Self when(User<T> actionType, Runnable listener) {
        return when(actionType, e -> listener.run());
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event> Self when(User<T> actionType, Consumer<T> listener) {
        when(actionType).to(listener::accept);
        return (Self) this;
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event, A> Self when(User<T> actionType, Consumer<A> listener, A context) {
        return when(actionType, e -> listener.accept(context));
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event> Self when(User<T> actionType, WiseBiConsumer<T, Self> listener) {
        return when(actionType, e -> listener.accept(e, (Self) this));
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    default <T extends Event, Context> Self when(User<T> actionType, Context context, WiseTriConsumer<T, Self, Context> listener) {
        return when(actionType, e -> listener.accept(e, (Self) this, context));
    }
}
