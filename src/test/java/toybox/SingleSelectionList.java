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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;

import kiss.Manageable;
import kiss.Singleton;
import kiss.Storable;

/**
 * @version 2018/03/04 22:45:17
 */
@Manageable(lifestyle = Singleton.class)
public abstract class SingleSelectionList<T> extends SingleSelectionModel<T> implements Storable<SingleSelectionList> {

    /** The item holder. */
    private ObservableList<T> items;

    private final ListChangeListener change = e -> store();

    /**
     * 
     */
    protected SingleSelectionList() {
        setItems(FXCollections.observableArrayList());
        restore();
    }

    /**
     * Get the items property of this {@link SingleSelectionList}.
     * 
     * @return The items property.
     */
    public ObservableList<T> getItems() {
        return items;
    }

    /**
     * Set the items property of this {@link SingleSelectionList}.
     * 
     * @param items The items value to set.
     */
    public void setItems(ObservableList<T> items) {
        if (this.items != null) {
            this.items.removeListener(change);
        }

        this.items = items;

        if (this.items != null) {
            this.items.addListener(change);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T getModelItem(int index) {
        return items.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getItemCount() {
        return items.size();
    }
}
