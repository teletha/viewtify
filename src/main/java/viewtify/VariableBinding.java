/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import javafx.beans.binding.ObjectBinding;

import kiss.Disposable;
import kiss.Variable;

/**
 * @version 2017/12/03 20:01:46
 */
public class VariableBinding<V> extends ObjectBinding<V> {

    /** The variable delegator. */
    private final Variable<V> variable;

    /** The binding disposer. */
    private final Disposable disposer;

    /**
     * @param variable
     * @param listeners
     */
    public VariableBinding(Variable<V> variable) {
        this.variable = variable;
        this.disposer = variable.observe().to(v -> invalidate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected V computeValue() {
        return variable.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();

        disposer.dispose();
    }
}