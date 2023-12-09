/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
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
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;

import kiss.I;
import kiss.Signal;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseFunction;
import kiss.WiseRunnable;

public interface UserActionHelper<Self extends UserActionHelper<Self>> {

    /**
     * Return the JavaFX's UI component.
     * 
     * @return A UI component
     */
    Object ui();

    /**
     * Shorthand method to register user action.
     * 
     * @param action
     */
    default Self action(WiseRunnable action) {
        return when(User.Action, action);
    }

    /**
     * Fire the specified event on this event target.
     * 
     * @param event An event to fire.
     * @return Chainable API
     */
    default Self fire(Event event) {
        if (ui() instanceof Node node) {
            node.fireEvent(event);
        }
        return (Self) this;
    }

    /**
     * Disable event propagation.
     * 
     * @param <E>
     * @param actionTypes A list of user action to disable.
     * @return
     */
    default <E extends Event> Self ignore(User<E>... actionTypes) {
        for (User<E> type : actionTypes) {
            when(type, e -> e.consume());
        }
        return (Self) this;
    }

    /**
     * Listen the specified user action.
     * 
     * @param actionType A user action to detect.
     * @return An event {@link Signal}.
     */
    default <E extends Event> Signal<E> when(User<E> actionType) {
        return actionType.hook.apply(this, new Signal<E>((observer, disposer) -> {
            EventHandler<E> listener = actionType.consumable == false ? observer::accept : e -> {
                observer.accept(e);
                e.consume();
            };

            invoke(actionType.before ? "addEventFilter" : "addEventHandler", ui(), actionType.type, listener);

            return disposer.add(() -> {
                invoke(actionType.before ? "removeEventFilter" : "removeEventHandler", ui(), actionType.type, listener);
            });
        }));
    }

    /**
     * Invoke the event handler regstration methods.
     * 
     * @param name A method name.
     * @param o A target object.
     * @param type An event type.
     * @param handler An event handler.
     */
    private void invoke(String name, Object o, EventType type, EventHandler handler) {
        try {
            o.getClass().getMethod(name, EventType.class, EventHandler.class).invoke(o, type, handler);
        } catch (Exception e) {
            throw I.quiet(e);
        }
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
        WiseConsumer wise = I.wiseC(listener);
        return (Self) when(actionType, wise);
    }

    /**
     * Listen all specified user actions.
     * 
     * @param actionType1 A user action to detect.
     * @param actionType2 A user action to detect.
     * @param listener An event listener.
     * @return Chainable API.
     */
    default <E1 extends Event, E2 extends Event> Self when(User<E1> actionType1, User<E2> actionType2, WiseRunnable listener) {
        WiseConsumer wise = I.wiseC(listener);
        return (Self) when(actionType1, wise).when(actionType2, wise);
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
    default <E1 extends Event, E2 extends Event, E3 extends Event> Self when(User<E1> actionType1, User<E2> actionType2, User<E3> actionType3, WiseRunnable listener) {
        WiseConsumer wise = I.wiseC(listener);
        return (Self) when(actionType1, wise).when(actionType2, wise).when(actionType3, wise);
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

    /**
     * Create temporary {@link UserActionHelper}.
     * 
     * @param eventTarget
     * @return
     */
    static UserActionHelper of(EventTarget eventTarget) {
        return () -> eventTarget;
    }
}