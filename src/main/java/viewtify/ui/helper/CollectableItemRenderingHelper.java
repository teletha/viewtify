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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;

public interface CollectableItemRenderingHelper<Self extends CollectableItemRenderingHelper, E> {

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self render(Function<E, String> renderer) {
        Objects.requireNonNull(renderer);
        return renderProperty(e -> new SimpleObjectProperty(renderer.apply(e)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderVariable(Function<E, Variable<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderProperty(e -> Viewtify.property(renderer.apply(e)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderProperty(Function<E, Property<String>> renderer) {
        return renderNode(e -> {
            Label label = new Label();
            label.textProperty().bind(renderer.apply(e));
            return label;
        });
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderUI(Function<E, ? extends UserInterfaceProvider<? extends Node>> renderer) {
        return renderNode(e -> renderer.apply(e).ui());
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    Self renderNode(Function<E, ? extends Node> renderer);
}
