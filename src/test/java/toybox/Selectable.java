/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import static java.util.concurrent.TimeUnit.*;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;

import kiss.Manageable;
import kiss.Observer;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import viewtify.Viewtify;

/**
 * @version 2018/03/11 17:54:53
 */
@Manageable(lifestyle = Singleton.class)
public abstract class Selectable<Self extends Selectable, T> implements Iterable<T>, Storable<Self> {

    /** The item manager. */
    private List<T> items = new CopyOnWriteArrayList();

    /** The addtion event manager. */
    private List<Observer<? super T>> adds = new CopyOnWriteArrayList<>();

    /** The addtion event manager. */
    public transient final Signal<T> add = new Signal<>(adds);

    /** The remove event manager. */
    private List<Observer<? super T>> removes = new CopyOnWriteArrayList<>();

    /** The remove event manager. */
    public transient final Signal<T> remove = new Signal<>(removes);

    /** The selection event manager. */
    private List<Observer<? super T>> selects = new CopyOnWriteArrayList<>();

    /** The selection event manager. */
    public transient final Signal<T> select = new Signal<>(selects);

    /** The deselection event manager. */
    private List<Observer<? super T>> deselects = new CopyOnWriteArrayList<>();

    /** The deselection event manager. */
    public transient final Signal<T> deselect = new Signal<>(deselects);

    /** The selected item index. */
    public final Variable<Integer> selectionIndex = Variable.of(-1);

    /**
     * Create {@link Selectable} model.
     */
    protected Selectable() {
        restore();
        add.merge(remove, select).debounce(1, SECONDS).to(this::store);
    }

    /**
     * Get selected item.
     * 
     * @return
     */
    public final T getSelection() {
        return getItems().get(selectionIndex.v);
    }

    /**
     * Get the items property of this {@link Selectable}.
     * 
     * @return The items property.
     */
    protected List<T> getItems() {
        return items;
    }

    /**
     * Set the items property of this {@link Selectable}.
     * 
     * @param items The items value to set.
     */
    protected void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * <p>
     * Deselect item.
     * </p>
     */
    public final void deselect() {
        select(-1);
    }

    /**
     * Select by index.
     * 
     * @param index The item index to select.
     */
    public final T select(int index) {
        int old = this.selectionIndex.v;
        int size = items.size();

        if (size == 0) {
            index = -1;
        } else if (index < 0) {
            index = 0;
        } else if (size <= index) {
            index = size - 1;
        }
        this.selectionIndex.set(index);

        // notify
        if (old != -1) {
            T item = items.get(old);
            deselects.forEach(o -> o.accept(item));
        }

        if (index != -1) {
            T item = items.get(index);
            selects.forEach(o -> o.accept(item));
        }
        return getSelection();
    }

    /**
     * Select by item.
     * 
     * @param item
     * @return
     */
    public final T select(T item) {
        return select(items.indexOf(item));
    }

    /**
     * <p>
     * Select next item.
     * </p>
     * 
     * @return
     */
    public final T selectNext() {
        select(selectionIndex.v + 1);

        return getSelection();
    }

    /**
     * <p>
     * Select previous item.
     * </p>
     * 
     * @return
     */
    public final T selectPrevious() {
        select(selectionIndex.v - 1);

        return getSelection();
    }

    /**
     * <p>
     * Select first item.
     * </p>
     * 
     * @return
     */
    public final T selectFirst() {
        select(0);

        return getSelection();
    }

    /**
     * <p>
     * Select last item.
     * </p>
     * 
     * @return
     */
    public final T selectLast() {
        select(items.size() - 1);

        return getSelection();
    }

    /**
     * <p>
     * Appends the specified item to the end of this model.
     * </p>
     * 
     * @param item An item to be appended to this model.
     * @return The index of the added item in this model.
     */
    public final int add(T item) {
        int index = items.indexOf(item);

        if (index == -1) {
            index = items.size();
            items.add(item);
            adds.forEach(o -> o.accept(item));
        }
        return index;
    }

    /**
     * <p>
     * Removes the first occurrence of the specified item from this model, if it is present . If
     * this model does not contain the item, it is unchanged.
     * </p>
     * 
     * @param item An item to be removed from this model, if present.
     */
    public final void remove(T item) {
        int index = items.indexOf(item);

        if (index != -1) {
            items.remove(index);

            // synchronize actual model item position and the selected item position
            if (index <= this.selectionIndex.v) {
                this.selectionIndex.set(v -> v - 1);
            }

            // At first, notify item removing.
            removes.forEach(o -> o.accept(item));

            // Then notify selection changing.
            select(index);
        }
    }

    /**
     * <p>
     * Returns the item at the specified position in this model.
     * </p>
     * 
     * @param index A index of the item to return
     * @return The item at the specified position in this model.
     */
    public final T get(int index) {
        return items.get(index);
    }

    /**
     * <p>
     * Returns the number of items in this model.
     * </p>
     * 
     * @return The number of items in this model.
     */
    public final int size() {
        return items.size();
    }

    /**
     * <p>
     * Returns the index of the first occurrence of the specified item in this list, or -1 if this
     * model does not contain the item.
     * </p>
     * 
     * @param item An item to search for.
     * @return The index of the first occurrence of the specified item in this model, or -1 if this
     *         list does not contain the item.
     */
    public final int indexOf(T item) {
        return items.indexOf(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<T> iterator() {
        return items.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        items.forEach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spliterator<T> spliterator() {
        return items.spliterator();
    }

    /**
     * Bind to the target {@link ObservableList}.
     * 
     * @param nodes
     */
    public final <N extends Node> void bind(ObservableList<N> nodes, Function<T, N> adding, Function<T, N> removing, ObservableValue<SelectionModel> selection) {
        if (nodes != null) {
            add.startWith(this).map(adding).to(nodes::add);
            remove.map(removing).to(nodes::remove);

            // selection
            selectionIndex.observeNow().to(selection.getValue()::select);
            Viewtify.calculate(selection).flatObservable(SelectionModel<Tab>::selectedIndexProperty).as(Integer.class).to(this::select);
        }
    }
}