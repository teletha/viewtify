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

import java.util.concurrent.CopyOnWriteArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;

import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.model.Model;
import kiss.model.Property;
import viewtify.Viewtify;

/**
 * @version 2018/03/07 11:03:13
 */
@Manageable(lifestyle = Singleton.class)
public abstract class SelectableModel<T> extends SingleSelectionModel<T> implements Storable<SelectableModel<T>> {

    /** The actual list. */
    public final ObservableList<T> items = FXCollections.observableList(new CopyOnWriteArrayList());

    /** The event signal. */
    private final Signal<Change<T>> changes = new Signal<Change<T>>((observer, disposer) -> {
        ListChangeListener<T> listener = c -> {
            while (c.next()) {
                observer.accept((Change<T>) c);
            }
        };

        items.addListener(listener);
        return disposer.add(() -> items.removeListener(listener));
    }).share();

    /** The event signal. */
    public transient final Signal<T> added = changes.take(Change::wasAdded).flatIterable(Change::getAddedSubList);

    /** The event signal. */
    public transient final Signal<T> removed = changes.take(Change::wasRemoved).flatIterable(Change::getRemoved);

    /** The event signal. */
    public transient final Signal<T> selected = Viewtify.signal(selectedItemProperty());

    /**
     * With initial values.
     * 
     * @param values
     */
    public SelectableModel(T... values) {
        Model<SelectableModel<T>> model = Model.of(this);

        for (Property p : model.properties()) {
            System.out.println(p.name);
        }
        items.addAll(values);
        restore();
        added.merge(removed, selected).debounce(1, SECONDS).to(this::store);
    }

    /**
     * Get the selectIndex property of this {@link SelectableModel}.
     * 
     * @return The selectIndex property.
     */
    int getWave() {
        return getSelectedIndex();
    }

    /**
     * Set the selectIndex property of this {@link SelectableModel}.
     * 
     * @param index The selectIndex value to set.
     */
    void setWave(int index) {
        select(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T getModelItem(int index) {
        try {
            return items.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getItemCount() {
        return items.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return items.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return items.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return items.toString();
    }
}
