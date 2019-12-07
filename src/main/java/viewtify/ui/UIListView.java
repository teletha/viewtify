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

import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.CollectableItemRenderingHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.MultiSelectableHelper;

public class UIListView<E> extends AbstractCollectableUI<UIListView<E>, ListView<E>, E>
        implements MultiSelectableHelper<UIListView<E>, E>, CollectableHelper<UIListView<E>, E>,
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
    public UIListView<E> renderByNode(Function<E, ? extends Node> renderer) {
        ui.setCellFactory(view -> new GenericCell<E>(renderer::apply));
        return this;
    }

    /**
     * 
     */
    private static class GenericCell<E> extends ListCell<E> {

        /** The user defined cell renderer. */
        private final Function<E, Node> renderer;

        /**
         * @param renderer
         */
        private GenericCell(Function<E, Node> renderer) {
            this.renderer = renderer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
            } else {
                setGraphic(renderer.apply(item));
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
