/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.concurrent.CopyOnWriteArrayList;

import kiss.Observer;
import kiss.Signal;

/**
 * @version 2018/03/13 2:04:23
 */
public class Switch<T> {

    /** The internal listeners. */
    private volatile CopyOnWriteArrayList<Observer<? super T>> observers;

    /** The exposed API. */
    public final Signal<T> expose = new Signal<>((observer, disposer) -> {
        if (observers == null) {
            observers = new CopyOnWriteArrayList();
        }
        observers.add(observer);

        return disposer.add(() -> {
            observers.remove(observer);

            if (observers.isEmpty()) {
                observers = null;
            }
        });
    });

    /**
     * Fire signal.
     * 
     * @param value
     */
    public final void emit(T value) {
        if (observers != null) {
            for (Observer<? super T> observer : observers) {
                observer.accept(value);
            }
        }
    }
}
