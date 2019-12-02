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
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UILabel;
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
        return renderByProperty(e -> new SimpleObjectProperty(renderer.apply(e)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self render(BiConsumer<UILabel, E> renderer) {
        Objects.requireNonNull(renderer);
        return renderByUI(e -> {
            UILabel label = UIBuilder.createUILabel();
            renderer.accept(label, e);
            return label;
        });
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderByVariable(Function<E, Variable<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByProperty(e -> Viewtify.property(renderer.apply(e)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self renderByProperty(Function<E, Property<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(e -> {
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
    default Self renderByUI(Function<E, ? extends UserInterfaceProvider<? extends Node>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(e -> renderer.apply(e).ui());
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    Self renderByNode(Function<E, ? extends Node> renderer);
}
