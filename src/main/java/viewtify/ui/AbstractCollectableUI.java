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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.PropertyAccessHelper;
import viewtify.ui.helper.ValueHelper;

public class AbstractCollectableUI<Self extends AbstractCollectableUI<Self, N, E>, N extends Node, E> extends UserInterface<Self, N>
        implements CollectableHelper<Self, E>, PropertyAccessHelper {

    /** The item holder. */
    private final Property<ObservableList<E>> items = new SimpleObjectProperty();

    /** The item taking filter. */
    private final Variable<Predicate<E>> filter = Variable.empty();

    /** The item sorter. */
    private final Variable<Comparator<E>> sorter = Variable.empty();

    /**
     * @param ui
     * @param view
     */
    protected AbstractCollectableUI(N ui, View view) {
        super(ui, view);
        this.items.setValue(property(Type.Items).getValue());

        Viewtify.observeNow(items).combineLatest(filter.observeNow(), sorter.observeNow()).to(v -> {
            v.to((items, filter, sorter) -> {
                if (filter != null) {
                    items = items.filtered(filter);
                }

                if (sorter != null) {
                    items = items.sorted(sorter);
                }
                property(Type.Items).setValue(items);
            });
        });
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
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    public final Self take(Predicate<E> filter) {
        this.filter.set(filter);
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    public final <C> Self take(ObservableValue<C> context, BiPredicate<E, C> filter) {
        Viewtify.observeNow(context).to(c -> take(e -> filter.test(e, c)));
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    public final <C> Self take(ValueHelper<?, C> context, BiPredicate<E, C> filter) {
        return take(context.valueProperty(), filter);
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    public final Self skip(Predicate<E> filter) {
        return take(filter.negate());
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    public final <C> Self skip(ObservableValue<C> context, BiPredicate<E, C> filter) {
        return take(context, filter.negate());
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    public final <C> Self skip(ValueHelper<?, C> context, BiPredicate<E, C> filter) {
        return take(context.valueProperty(), filter.negate());
    }
}
