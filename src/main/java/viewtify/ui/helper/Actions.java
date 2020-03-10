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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;

import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseTriFunction;
import viewtify.ui.helper.PropertyAccessHelper.Type;

public class Actions {

    /**
     * Create new filter to take the mouse event only inside the specified {@link Node}.
     * 
     * @param node A target node.
     * @return A filter.
     */
    public static Predicate<GestureEvent> inside(Supplier<Node> node) {
        return e -> node.get().contains(e.getX(), e.getY());
    }

    /**
     * Select the next item. (skip disabled item)
     * 
     * @param model
     */
    public static boolean selectNext(SelectionModel model) {
        Object current = model.getSelectedItem();
        model.selectNext();
        Object next = model.getSelectedItem();

        if (current != next && next instanceof PropertyAccessHelper) {
            PropertyAccessHelper helper = (PropertyAccessHelper) next;
            Property<Boolean> property = helper.property(Type.Disable);
            if (property.getValue()) {
                return selectNext(model);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Select the previous item. (skip disabled item)
     * 
     * @param model
     */
    public static boolean selectPrev(SelectionModel model) {
        Object current = model.getSelectedItem();
        model.selectPrevious();
        Object prev = model.getSelectedItem();

        if (current != prev && prev instanceof PropertyAccessHelper) {
            PropertyAccessHelper helper = (PropertyAccessHelper) prev;
            Property<Boolean> property = helper.property(Type.Disable);
            if (property.getValue()) {
                return selectPrev(model);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @param model A selection model to traverse.
     * @return New action.
     */
    public static WiseConsumer<ScrollEvent> traverse(SelectionModel model) {
        return e -> {
            if (e.getDeltaY() < 0) {
                selectNext(model);
            } else {
                selectPrev(model);
            }
            e.consume();
        };
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return New action.
     */
    public static <X extends ValueHelper<X, Integer>> WiseBiConsumer<ScrollEvent, X> traverseInt() {
        return traverseInt(1);
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return step Difference value when increasing or decreasing the value.
     * @return New action.
     */
    public static <X extends ValueHelper<X, Integer>> WiseBiConsumer<ScrollEvent, X> traverseInt(int step) {
        return traverseInt(() -> step);
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return step Difference value when increasing or decreasing the value.
     * @return New action.
     */
    public static <X extends ValueHelper<X, Integer>> WiseBiConsumer<ScrollEvent, X> traverseInt(Supplier<Integer> step) {
        return traverse((value, diff, amplified) -> value + diff * step.get() * (amplified ? 10 : 1));
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return New action.
     */
    public static <X extends ValueHelper<X, Double>> WiseBiConsumer<ScrollEvent, X> traverseDouble() {
        return traverseDouble(0.01);
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return step Difference value when increasing or decreasing the value.
     * @return New action.
     */
    public static <X extends ValueHelper<X, Double>> WiseBiConsumer<ScrollEvent, X> traverseDouble(double step) {
        return traverseDouble(() -> step);
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return step Difference value when increasing or decreasing the value.
     * @return New action.
     */
    public static <X extends ValueHelper<X, Double>> WiseBiConsumer<ScrollEvent, X> traverseDouble(Supplier<Double> step) {
        return traverse((value, diff, amplified) -> {
            return BigDecimal.valueOf(value)
                    .add(BigDecimal.valueOf(step.get())
                            .multiply(BigDecimal.valueOf(diff))
                            .multiply(amplified ? BigDecimal.TEN : BigDecimal.ONE))
                    .doubleValue();
        });
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @return New action.
     */
    public static <X extends ValueHelper<X, LocalDate>> WiseBiConsumer<ScrollEvent, X> traverseLocalDate() {
        return traverse((value, diff, amplified) -> value.plus(diff, amplified ? ChronoUnit.MONTHS : ChronoUnit.DAYS));
    }

    /**
     * Creates an action that changes the value when a scroll event occurs.
     * 
     * @param traverser A value traverser with value difference and amplifier.
     * @return New action.
     */
    public static <X extends ValueHelper<X, V>, V> WiseBiConsumer<ScrollEvent, X> traverse(WiseTriFunction<V, Integer, Boolean, V> traverser) {
        return (e, helper) -> {
            if (e.getDeltaY() > 0) {
                helper.value(traverser.apply(helper.value(), 1, false));
            } else if (e.getDeltaY() < 0) {
                helper.value(traverser.apply(helper.value(), -1, false));
            } else if (e.getDeltaX() > 0) {
                helper.value(traverser.apply(helper.value(), 1, true));
            } else if (e.getDeltaX() < 0) {
                helper.value(traverser.apply(helper.value(), -1, true));
            }
        };
    }
}
