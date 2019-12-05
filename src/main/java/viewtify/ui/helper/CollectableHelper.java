/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public interface CollectableHelper<Self extends CollectableHelper<Self, E>, E> {

    /**
     * Returns the managed item list.
     * 
     * @return
     */
    Property<ObservableList<E>> itemProperty();

    /**
     * Get the all items.
     * 
     * @return A live item list.
     */
    default ObservableList<E> items() {
        return itemProperty().getValue();
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(E... items) {
        return items(List.of(items));
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(Iterable<E> items) {
        return items(I.signal(items));
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(Signal<E> items) {
        return items(items.toList());
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(Stream<E> items) {
        return items(items.collect(Collectors.toList()));
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(List<E> items) {
        modifyItemUISafely(list -> {
            list.clear();
            list.setAll(items);
        });
        return (Self) this;
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(ObservableList<E> items) {
        if (items != null) {
            modifyItemUISafely(list -> itemProperty().setValue(items));
        }
        return (Self) this;
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(E... initialItems) {
        return initialize(List.of(initialItems));
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(Signal<E> initialItems) {
        return initialize(initialItems.toList());
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(Stream<E> initialItems) {
        return initialize(initialItems.collect(Collectors.toList()));
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(List<E> initialItems) {
        items(initialItems);
    
        if (this instanceof ValueHelper && initialItems != null && !initialItems.isEmpty()) {
            ((ValueHelper) this).initialize(initialItems.get(0));
        }
        return (Self) this;
    }

    /**
     * Returns the first item.
     * 
     * @return
     */
    default Variable<E> first() {
        ObservableList<E> items = itemProperty().getValue();

        if (items.isEmpty()) {
            return Variable.empty();
        } else {
            return Variable.of(items.get(0));
        }
    }

    /**
     * Returns the last item.
     * 
     * @return
     */
    default Variable<E> last() {
        ObservableList<E> items = itemProperty().getValue();

        if (items.isEmpty()) {
            return Variable.empty();
        } else {
            return Variable.of(items.get(items.size() - 1));
        }
    }

    /**
     * Return the number of items.
     * 
     * @return
     */
    default int size() {
        return itemProperty().getValue().size();
    }

    /**
     * Add the specified item.
     * 
     * @param index An index to add.
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self addItemAt(int index, E item) {
        if (item != null && 0 <= index) {
            modifyItemUISafely(list -> list.add(Math.min(index, list.size()), item));
        }
        return (Self) this;
    }

    /**
     * Add the specified item at the first.
     * 
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self addItemAtFirst(E item) {
        if (item != null) {
            modifyItemUISafely(list -> list.add(0, item));
        }
        return (Self) this;
    }

    /**
     * Add the specified item at the last.
     * 
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self addItemAtLast(E item) {
        if (item != null) {
            modifyItemUISafely(list -> list.add(item));
        }
        return (Self) this;
    }

    /**
     * Remove the specified item.
     * 
     * @param item An item to remove.
     * @return Chainable API.
     */
    default Self removeItem(E item) {
        if (item != null) {
            modifyItemUISafely(list -> {
                Iterator<E> iterator = list.iterator();

                while (iterator.hasNext()) {
                    E next = iterator.next();

                    if (next == item) {
                        iterator.remove();
                        break;
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Remove all items.
     * 
     * @return
     */
    default Self removeItemAll() {
        modifyItemUISafely(List<E>::clear);
        return (Self) this;
    }

    /**
     * Remove the first item.
     * 
     * @return Chainable API.
     */
    default Self removeItemAtFirst() {
        modifyItemUISafely(list -> {
            Iterator<E> iterator = list.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        });
        return (Self) this;
    }

    /**
     * Remove the last item.
     * 
     * @return Chainable API.
     */
    default Self removeItemAtLast() {
        modifyItemUISafely(list -> {
            ListIterator<E> iterator = list.listIterator(list.size());
            if (iterator.hasPrevious()) {
                iterator.previous();
                iterator.remove();
            }
        });
        return (Self) this;
    }

    /**
     * Modify items in UI thread.
     * 
     * @param action
     */
    private void modifyItemUISafely(Consumer<ObservableList<E>> action) {
        Viewtify.inUI(() -> action.accept(itemProperty().getValue()));
    }
}
