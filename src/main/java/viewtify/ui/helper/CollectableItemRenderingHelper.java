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

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public interface CollectableItemRenderingHelper<Self extends CollectableItemRenderingHelper, E> {

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self render(Function<E, String> renderer) {
        Objects.requireNonNull(renderer);
        return renderSignal(value -> I.signal(renderer.apply(value)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderValue(Function<E, ObservableValue<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderSignal(value -> Viewtify.observeNow(renderer.apply(value)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderVariable(Function<E, Variable<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderSignal(value -> renderer.apply(value).observeNow());
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    Self renderSignal(Function<E, Signal<String>> renderer);

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderCheckbox(Function<E, String> renderer, Function<E, Variable<Boolean>> checked) {
        Objects.requireNonNull(renderer);
        Objects.requireNonNull(checked);
        return renderCheckboxSignal(value -> I.signal(renderer.apply(value)), checked);
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderCheckboxValue(Function<E, ObservableValue<String>> renderer, Function<E, Variable<Boolean>> checked) {
        Objects.requireNonNull(renderer);
        Objects.requireNonNull(checked);
        return renderCheckboxSignal(value -> Viewtify.observeNow(renderer.apply(value)), checked);
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderCheckboxVariable(Function<E, Variable<String>> renderer, Function<E, Variable<Boolean>> checked) {
        Objects.requireNonNull(renderer);
        Objects.requireNonNull(checked);
        return renderCheckboxSignal(value -> renderer.apply(value).observeNow(), checked);
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    Self renderCheckboxSignal(Function<E, Signal<String>> renderer, Function<E, Variable<Boolean>> checked);
}
