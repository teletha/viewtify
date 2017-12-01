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

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;

/**
 * @version 2017/12/02 0:51:04
 */
public abstract class PreboundBinding<T> extends ObjectBinding<T> implements MonadicBinding<T> {
    private final Observable[] dependencies;

    public PreboundBinding(Observable... dependencies) {
        this.dependencies = dependencies;
        bind(dependencies);
    }

    @Override
    public void dispose() {
        unbind(dependencies);
    }
}
