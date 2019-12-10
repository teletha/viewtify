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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.BaseStream;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public interface CollectableHelper<Self extends ReferenceHolder & CollectableHelper<Self, E>, E> {

    /**
     * Returns the managed item list.
     * 
     * @return
     */
    Property<ObservableList<E>> itemsProperty();

    /**
     * Get the all items.
     * 
     * @return A live item list.
     */
    default ObservableList<E> items() {
        return refer().items.getValue();
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
    default Self items(Signal<E> items) {
        return items(items.toList());
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(BaseStream<E, ?> items) {
        return items(items::iterator);
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
            modifyItemUISafely(list -> refer().items.setValue(items));
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
    default Self initialize(BaseStream<E, ?> initialItems) {
        return initialize(initialItems::iterator);
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(Iterable<E> initialItems) {
        return initialize(I.signal(initialItems));
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
        ObservableList<E> items = refer().items.getValue();

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
        ObservableList<E> items = refer().items.getValue();

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
        return refer().items.getValue().size();
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
        Viewtify.inUI(() -> action.accept(refer().items.getValue()));
    }

    /**
     * Sort items by the specified {@link Comparator}.
     * 
     * @param sorter A item comparator.
     * @return Chainable API.
     */
    default Self sort(Comparator<E> sorter) {
        refer().sorter.set(sorter);
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default Self take(Predicate<E> filter) {
        refer().filter.set(filter);
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default <C> Self take(ObservableValue<C> context, BiPredicate<E, C> filter) {
        Viewtify.observing(context).to(c -> take(e -> filter.test(e, c)));
        return (Self) this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default <C> Self take(ValueHelper<?, C> context, BiPredicate<E, C> filter) {
        return take(context.valueProperty(), filter);
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default Self skip(Predicate<E> filter) {
        return take(filter.negate());
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default <C> Self skip(ObservableValue<C> context, BiPredicate<E, C> filter) {
        return take(context, filter.negate());
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param context An additional infomation.
     * @param filter A conditional filter.
     * @return Chainable API.
     */
    default <C> Self skip(ValueHelper<?, C> context, BiPredicate<E, C> filter) {
        return take(context.valueProperty(), filter.negate());
    }

    /**
     * Retrieve the special reference holder.
     * 
     * @return
     */
    private Ð<E> refer() {
        ReferenceHolder holder = (ReferenceHolder) this;

        if (holder.collectable == null) {
            synchronized (this) {
                if (holder.collectable == null) {
                    holder.collectable = new Ð(this);
                }
            }
        }
        return holder.collectable;
    }

    /**
     * 
     */
    final class Ð<E> {

        /** The item holder. */
        private final Property<ObservableList<E>> items = new SimpleObjectProperty();

        /** The item taking filter. */
        private final Variable<Predicate<E>> filter = Variable.empty();

        /** The item sorter. */
        private final Variable<Comparator<E>> sorter = Variable.empty();

        /**
         * Initialize date reference.
         * 
         * @param helper
         */
        private Ð(CollectableHelper<?, E> helper) {
            items.setValue(helper.itemsProperty().getValue());

            Viewtify.observing(items).combineLatest(filter.observing(), sorter.observing()).to(v -> {
                ObservableList items = v.ⅰ;

                if (v.ⅱ != null) {
                    items = items.filtered(v.ⅱ);
                }

                if (v.ⅲ != null) {
                    items = items.sorted(v.ⅲ);
                }
                helper.itemsProperty().setValue(items);
            });
        }
    }
}
