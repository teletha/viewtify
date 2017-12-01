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

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 * @version 2017/12/02 0:52:24
 */
public interface PropertyBinding<T> extends Property<T>, MonadicBinding<T> {

    /**
     * Like {@link #bind(ObservableValue)}, plus whenever the underlying property changes, the
     * previous one is set to the provided value.
     */
    void bind(ObservableValue<? extends T> observable, T resetToOnUnbind);
}
