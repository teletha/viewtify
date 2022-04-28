/*
 * Copyright (C) 2021 viewtify Development Team
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

import kiss.Disposable;
import kiss.Variable;
import kiss.WiseTriConsumer;
import kiss.WiseTriFunction;
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
        if (this instanceof CollectableValuedItemRenderingHelper) {
            ((CollectableValuedItemRenderingHelper) this).renderSelected(renderer);
        }
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
        return renderByUI(() -> new UILabel(null), (label, e, d) -> {
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
    default <C> Self renderByVariable(Function<E, Variable<String>> renderer) {
        if (this instanceof CollectableValuedItemRenderingHelper) {
            ((CollectableValuedItemRenderingHelper) this).renderSelectedByVariable(renderer);
        }
        return renderByProperty(() -> new UILabel(null), (c, e) -> Viewtify.property(renderer.apply(e)));
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
    default <C> Self renderByProperty(Function<E, Property<String>> renderer) {
        return renderByProperty(() -> new UILabel(null), (c, e) -> renderer.apply(e));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    default <C> Self renderByProperty(Supplier<C> context, BiFunction<C, E, Property<String>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(context, (c, e, d) -> {
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
    default <C> Self renderByUI(Supplier<C> context, WiseTriFunction<C, E, Disposable, ? extends UserInterfaceProvider<? extends Node>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(context, (c, e, d) -> renderer.apply(c, e, d).ui());
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    <C> Self renderByNode(Supplier<C> context, WiseTriFunction<C, E, Disposable, ? extends Node> renderer);

    default Self renderAsCheckBox(Function<E, Variable<Boolean>> modeler, WiseTriConsumer<UICheckBox, Variable<Boolean>, Disposable> renderer) {
        return renderByUI(() -> new UICheckBox(null), (ui, value, disposer) -> {
            renderer.accept(ui, modeler.apply(value), disposer);
            return ui;
        });
    }

    default <V> Self renderAsComboBox(Function<E, Variable<V>> modeler, WiseTriConsumer<UIComboBox<V>, Variable<V>, Disposable> renderer) {
        return renderByUI(() -> new UIComboBox<V>(null), (ui, value, disposer) -> {
            renderer.accept(ui, modeler.apply(value), disposer);
            return ui;
        });
    }
}