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
    Property<ObservableList<E>> items();

    /**
     * Set the specified items.
     * 
     * @param items
     * @return
     */
    default Self items(ObservableList<E> items) {
        if (items != null) {
            items().setValue(items);
        }
        return (Self) this;
    }

    /**
     * Returns the first item.
     * 
     * @return
     */
    default Variable<E> first() {
        ObservableList<E> items = items().getValue();

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
        ObservableList<E> items = items().getValue();

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
        return items().getValue().size();
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
            modifyItemUISafely(list -> {
                if (index < list.size()) {
                    list.add(index, item);
                }
            });
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
    private void modifyItemUISafely(Consumer<List<E>> action) {
        Viewtify.inUI(() -> action.accept(items().getValue()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default <T extends Enum> Self values(Class<T> enums) {
        return values((E[]) enums.getEnumConstants());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(E... values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Iterable<E> values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Signal<E> values) {
        return values(values.toList());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Stream<E> values) {
        return values(values.collect(Collectors.toList()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(List<E> values) {
        ObservableList<E> list = items().getValue();
        list.clear();
        list.addAll(values);
        return (Self) this;
    }

    default Self initial(int index) {
        if (this instanceof PreferenceHelper) {
            ((PreferenceHelper) this).initial(items().getValue().get(index));
        }
        return (Self) this;
    }
}
