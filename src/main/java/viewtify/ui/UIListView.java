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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import kiss.I;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.PreferenceHelper;

public class UIListView<E> extends UserInterface<UIListView, ListView<E>>
        implements CollectableHelper<UIListView<E>, E>, ContextMenuHelper<UIListView> {

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

    public UIListView<E> cell(ListCell<E> cell) {
        ui.setCellFactory(e -> cell);
        return this;
    }

    public UIListView<E> cell(Function<UIListView<E>, ListCell<E>> factory) {
        ui.setCellFactory(e -> factory.apply(this));
        return this;
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
