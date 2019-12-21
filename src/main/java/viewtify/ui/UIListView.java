/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.CollectableItemRenderingHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;

public class UIListView<E> extends UserInterface<UIListView<E>, ListView<E>>
        implements SelectableHelper<UIListView<E>, E>, CollectableHelper<UIListView<E>, E>,
        CollectableItemRenderingHelper<UIListView<E>, E>, ContextMenuHelper<UIListView<E>> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UIListView(View view) {
        super(new ListView<E>(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<E>> itemsProperty() {
        return ui.itemsProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> UIListView<E> renderByNode(Supplier<C> context, BiFunction<C, E, ? extends Node> renderer) {
        ui.setCellFactory(view -> new GenericCell<C, E>(context, renderer));
        return this;
    }

    /**
     * 
     */
    private static class GenericCell<C, E> extends ListCell<E> {

        /** The context. */
        private final C context;

        /** The user defined cell renderer. */
        private final BiFunction<C, E, ? extends Node> renderer;

        /**
         * @param renderer
         */
        private GenericCell(Supplier<C> context, BiFunction<C, E, ? extends Node> renderer) {
            this.context = context.get();
            this.renderer = renderer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);

            setText(null);
            if (item == null || empty) {
                setGraphic(null);
            } else {
                setGraphic(renderer.apply(context, item));
            }
        }
    }

    /**
     * Scroll helper.
     * 
     * @return
     */
    public UIListView<E> scrollTo(int index) {
        ui.scrollTo(index);
        return this;
    }

    /**
     * Scroll helper.
     * 
     * @return
     */
    public UIListView<E> scrollToBottom() {
        return scrollTo(ui.getItems().size() - 1);
    }

    /**
     * Scroll helper.
     * 
     * @return
     */
    public UIListView<E> scrollToTop() {
        return scrollTo(0);
    }
}
