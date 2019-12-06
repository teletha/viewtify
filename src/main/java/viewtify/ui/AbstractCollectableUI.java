/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.CollectableHelper;

public abstract class AbstractCollectableUI<Self extends AbstractCollectableUI<Self, N, E>, N extends Node, E>
        extends UserInterface<Self, N>
        implements CollectableHelper<Self, E> {

    /** The item filter manager. */
    private final Variable<Predicate<E>> filter = Variable.empty();

    /** The item sorter. */
    private final Variable<Comparator> sorter = Variable.empty();

    /** The item list holder. */
    private final ObjectProperty<ObservableList<E>> items = new SimpleObjectProperty(FXCollections.observableArrayList());

    /**
     * @param ui
     * @param view
     */
    protected AbstractCollectableUI(N ui, View view) {
        super(ui, view);

        Viewtify.observeNow(items).combineLatest(filter.observeNow(), sorter.observeNow()).to(e -> e.to((items, filter, sorter) -> {
            if (filter != null) {
                items = items.filtered(filter);
            }

            if (sorter != null) {
                items = items.sorted(sorter);
                System.out.println("sorter " + sorter);
            }
            ui.setItems(items);
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property<ObservableList<E>> itemProperty() {
        return items;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <C> Self take(ObservableValue<C> context, BiPredicate<E, C> filter) {
        Viewtify.observeNow(context).to(c -> {
            this.filter.set((E e) -> filter.test(e, c));
        });
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <C> Self skip(ObservableValue<C> context, BiPredicate<E, C> filter) {
        return take(context, filter.negate());
    }
}
