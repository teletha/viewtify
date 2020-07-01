/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.Label;

import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UICheckBox;
import viewtify.ui.UIComboBox;
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
        return render((label, e) -> label.text(renderer.apply(e)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default Self render(BiConsumer<UILabel, E> renderer) {
        Objects.requireNonNull(renderer);
        return renderByUI(() -> new UILabel(null), (label, e) -> {
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
    default <C> Self renderByVariable(Supplier<C> context, BiFunction<C, E, Variable<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByProperty(context, (c, e) -> Viewtify.property(renderer.apply(c, e)));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default <C> Self renderByProperty(Supplier<C> context, BiFunction<C, E, Property<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(context, (c, e) -> {
            Label label = new Label();
            label.textProperty().bind(renderer.apply(c, e));
            return label;
        });
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default <C> Self renderByUI(Supplier<C> context, BiFunction<C, E, ? extends UserInterfaceProvider<? extends Node>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(context, (c, e) -> renderer.apply(c, e).ui());
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    <C> Self renderByNode(Supplier<C> context, BiFunction<C, E, ? extends Node> renderer);

    default Self renderAsCheckBox(Function<E, Variable<Boolean>> modeler, BiConsumer<UICheckBox, Variable<Boolean>> renderer) {
        return renderByUI(() -> new UICheckBox(null), (ui, value) -> {
            renderer.accept(ui, modeler.apply(value));
            return ui;
        });
    }

    default <V> Self renderAsComboBox(Function<E, Variable<V>> modeler, BiConsumer<UIComboBox<V>, Variable<V>> renderer) {
        return renderByUI(() -> new UIComboBox(null), (ui, value) -> {
            renderer.accept(ui, modeler.apply(value));
            return ui;
        });
    }
}