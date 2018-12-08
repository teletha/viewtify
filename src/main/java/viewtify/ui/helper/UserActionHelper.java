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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;

import kiss.I;
import kiss.Signal;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseFunction;
import kiss.WiseRunnable;

/**
 * @version 2018/08/04 1:10:45
 */
public interface UserActionHelper<Self extends UserActionHelper> {

    /**
     * Return the target {@link Node} to listen to user actions.
     * 
     * @return A target {@link Node}.
     */
    Node ui();

    /**
     * Listen the specified user action.
     * 
     * @param actionType A user action to detect.
     * @return An event {@link Signal}.
     */
    default <E extends Event> Signal<E> when(User<E> actionType) {
        return actionType.hook.apply(this, new Signal<E>((observer, disposer) -> {
            EventHandler<E> listener = observer::accept;

            ui().addEventHandler(actionType.type, listener);

            return disposer.add(() -> {
                ui().removeEventHandler(actionType.type, listener);
            });
        }));
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionTypes A list of user actions to detect.
     * @return An event {@link Signal}.
     */
    default <E extends Event> Signal<E> when(User<E>... actionTypes) {
        return when(I.signal(actionTypes));
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionTypes A list of user actions to detect.
     * @return An event {@link Signal}.
     */
    private <E extends Event> Signal<E> when(Signal<User<E>> actionTypes) {
        return actionTypes.skipNull().flatMap((WiseFunction<User<E>, Signal<E>>) this::when);
    }

    /**
     * Listen the specified user action.
     * 
     * @param actionType A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType, WiseRunnable listener) {
        return when(I.signal(actionType), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType1, User<E> actionType2, WiseRunnable listener) {
        return when(I.signal(actionType1, actionType2), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param actionType3 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType1, User<E> actionType2, User<E> actionType3, WiseRunnable listener) {
        return when(I.signal(actionType1, actionType2, actionType3), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionTypes A list of user actions to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    private <E extends Event> Self when(Signal<User<E>> actionTypes, WiseRunnable listener) {
        return when(actionTypes, listener.asConsumer());
    }

    /**
     * Listen the specified user action.
     * 
     * @param actionType A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType, WiseConsumer<E> listener) {
        return when(I.signal(actionType), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType1, User<E> actionType2, WiseConsumer<E> listener) {
        return when(I.signal(actionType1, actionType2), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param actionType3 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType1, User<E> actionType2, User<E> actionType3, WiseConsumer<E> listener) {
        return when(I.signal(actionType1, actionType2, actionType3), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionTypes A list of user actions to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    private <E extends Event> Self when(Signal<User<E>> actionTypes, WiseConsumer<E> listener) {
        when(actionTypes).to(listener);
        return (Self) this;
    }

    /**
     * Listen the specified user action.
     * 
     * @param actionType A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType, WiseBiConsumer<E, Self> listener) {
        return when(I.signal(actionType), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType1, User<E> actionType2, WiseBiConsumer<E, Self> listener) {
        return when(I.signal(actionType1, actionType2), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param actionType3 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E extends Event> Self when(User<E> actionType1, User<E> actionType2, User<E> actionType3, WiseBiConsumer<E, Self> listener) {
        return when(I.signal(actionType1, actionType2, actionType3), listener);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionTypes A list of user actions to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    private <E extends Event> Self when(Signal<User<E>> actionTypes, WiseBiConsumer<E, Self> listener) {
        return when(actionTypes, e -> listener.accept(e, (Self) this));
    }
}
