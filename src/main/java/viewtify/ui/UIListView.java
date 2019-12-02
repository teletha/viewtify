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

import java.util.ConcurrentModificationException;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.CollectableItemRenderingHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.PreferenceHelper;

public class UIListView<E> extends UserInterface<UIListView, ListView<E>>
        implements CollectableHelper<UIListView<E>, E>, CollectableItemRenderingHelper<UIListView<E>, E>, ContextMenuHelper<UIListView<E>> {

    /** The item filter manager. */
    private final Variable<Predicate<E>> filter;

    /** The item list manager. */
    private final ObjectProperty<ObservableList<E>> items;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UIListView(View view) {
        super(new ListView<E>(), view);

        this.filter = Variable.of(I.accept());
        this.items = new SimpleObjectProperty(ui.getItems());

        Viewtify.observe(items).combineLatest(filter.observeNow()).retry(ConcurrentModificationException.class).to(e -> {
            ui.setItems(e.ⅰ.filtered(e.ⅱ));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<E>> items() {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIListView<E> renderSignal(Function<E, Signal<String>> renderer) {
        ui.setCellFactory(listView -> {
            ListCell<E> cell = new ListCell();
            Viewtify.observeNow(cell.itemProperty()).skipNull().flatMap(renderer::apply).to(cell::setText);
            return cell;
        });
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIListView<E> renderCheckboxSignal(Function<E, Signal<String>> renderer, Function<E, Variable<Boolean>> checked) {
        ui.setCellFactory(view -> {
            return new GenericCell<E>(e -> {
                CheckBox box = new CheckBox();
                I.signal(e).skipNull().flatMap(renderer::apply).to(box::setText);
                box.selectedProperty().bindBidirectional(Viewtify.property(checked.apply(e)));
                return box;
            });
        });
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

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <C> UIListView<E> take(PreferenceHelper<?, C> context, BiPredicate<E, C> filter) {
        return take(context.model(), filter);
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <C> UIListView<E> take(ObservableValue<C> context, BiPredicate<E, C> filter) {
        Viewtify.observeNow(context).to(c -> {
            this.filter.set((E e) -> filter.test(e, c));
        });

        return this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <C> UIListView<E> skip(PreferenceHelper<?, C> context, BiPredicate<E, C> filter) {
        return take(context, filter.negate());
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <C> UIListView<E> skip(ObservableValue<C> context, BiPredicate<E, C> filter) {
        return take(context, filter.negate());
    }
}
