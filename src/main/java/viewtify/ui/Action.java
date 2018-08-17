/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Predicate;

import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;

import kiss.WiseConsumer;

/**
 * @version 2018/07/29 17:05:42
 */
public class Action {

    /**
     * Create new filter to take the mouse event only inside the specified {@link Node}.
     * 
     * @param node A target node.
     * @return A filter.
     */
    public static final Predicate<GestureEvent> inside(Node node) {
        return e -> node.contains(e.getX(), e.getY());
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
}
