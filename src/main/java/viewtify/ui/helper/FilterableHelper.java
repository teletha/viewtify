/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import viewtify.Viewtify;

public interface FilterableHelper<Self extends FilterableHelper, E> {

    Property<Predicate<E>> filterProperty();

    /**
     * Filter items by the specified condition.
     * 
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default Self take(Predicate<E> filter) {
        filterProperty().setValue(filter);
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default <C> Self take(ObservableValue<C> context, BiPredicate<E, C> filter) {
        Viewtify.observeNow(context).to(c -> take(e -> filter.test(e, c)));
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default Self skip(Predicate<E> filter) {
        return take(filter.negate());
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default <C> Self skip(ObservableValue<C> context, BiPredicate<E, C> filter) {
        return take(context, filter.negate());
    }
}
