/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;

import kiss.WiseBiConsumer;
import kiss.WiseConsumer;

public class Actions {

    /**
     * Create new filter to take the mouse event only inside the specified {@link Node}.
     * 
     * @param node A target node.
     * @return A filter.
     */
    public static final Predicate<GestureEvent> inside(Supplier<Node> node) {
        return e -> node.get().contains(e.getX(), e.getY());
    }

    /**
     * Create new action to traverse the {@link SelectionModel} by scroll.
     * 
     * @param model A target {@link SelectionModel}.
     * @return New action.
     */
    public static final WiseConsumer<ScrollEvent> traverse(SelectionModel model) {
        return e -> {
            if (e.getDeltaY() < 0) {
                model.selectNext();
            } else {
                model.selectPrevious();
            }
        };
    }

    /**
     * Create new action to traverse the {@link SelectionModel} by scroll.
     * 
     * @param model A target {@link SelectionModel}.
     * @return New action.
     */
    public static final <X extends ValueHelper<X, V>, V> WiseBiConsumer<ScrollEvent, X> traverse(UnaryOperator<V> increment, UnaryOperator<V> decrement) {
        return (e, helper) -> {
            if (e.getDeltaY() > 0) {
                helper.value(increment.apply(helper.value()));
            } else {
                helper.value(decrement.apply(helper.value()));
            }
        };
    }

    /**
     * Create new action to traverse the {@link SelectionModel} by scroll.
     * 
     * @param model A target {@link SelectionModel}.
     * @return New action.
     */
    public static final <X extends ValueHelper<X, Integer>> WiseBiConsumer<ScrollEvent, X> traverseInt(Supplier<Integer> step) {
        return (e, helper) -> {
            if (e.getDeltaY() > 0) {
                helper.value(v -> v + step.get());
            } else {
                helper.value(v -> v - step.get());
            }
        };
    }

    /**
     * Create new action to traverse the {@link SelectionModel} by scroll.
     * 
     * @param model A target {@link SelectionModel}.
     * @return New action.
     */
    public static final <X extends ValueHelper<X, Double>> WiseBiConsumer<ScrollEvent, X> traverseDouble(Supplier<Double> step) {
        return (e, helper) -> {
            if (e.getDeltaY() > 0) {
                helper.value(v -> v + step.get());
            } else {
                helper.value(v -> v - step.get());
            }
        };
    }
}
