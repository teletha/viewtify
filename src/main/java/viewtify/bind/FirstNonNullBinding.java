/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.bind;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

/**
 * @version 2017/12/02 0:53:37
 */

class FirstNonNullBinding<T> extends ObjectBinding<T> implements MonadicBinding<T> {
    private final ObservableValue<? extends T>[] chain;

    private final InvalidationListener listener = obs -> srcInvalidated(obs);

    private final InvalidationListener weakListener = new WeakInvalidationListener(listener);

    private int startAt = 0;

    @SafeVarargs
    public FirstNonNullBinding(ObservableValue<? extends T>... chain) {
        this.chain = chain;
        for (int i = 0; i < chain.length; ++i) {
            chain[i].addListener(weakListener);
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < chain.length; ++i) {
            chain[i].removeListener(weakListener);
        }
    }

    @Override
    protected T computeValue() {
        for (int i = startAt; i < chain.length; ++i) {
            T val = chain[i].getValue();
            if (val != null) {
                startAt = i;
                return val;
            }
        }
        startAt = chain.length;
        return null;
    }

    private void srcInvalidated(Observable src) {
        for (int i = 0; i < chain.length; ++i) {
            if (chain[i] == src) {
                srcInvalidated(i);
                break;
            }
        }
    }

    private void srcInvalidated(int index) {
        if (index <= startAt) {
            startAt = index;
            invalidate();
        }
    }
}
