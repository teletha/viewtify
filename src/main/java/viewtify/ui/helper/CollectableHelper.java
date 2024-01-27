/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Signaling;
import kiss.Variable;
import kiss.WiseConsumer;
import viewtify.Viewtify;
import viewtify.edit.Edito;
import viewtify.property.SmartProperty;
import viewtify.ui.UserInterface;
import viewtify.ui.query.CompoundQuery;
import viewtify.util.GuardedOperation;
import viewtify.util.Translatable;

public interface CollectableHelper<Self extends ReferenceHolder & CollectableHelper<Self, E>, E> {

    /**
     * Dispose by self.
     * 
     * @param disposable
     * @return
     */
    private Self dispose(Disposable disposable) {
        if (this instanceof Disposable disposer) {
            disposer.add(disposable);
        }
        return (Self) this;
    }

    /**
     * Returns the managed item list.
     * 
     * @return
     */
    Property<ObservableList<E>> itemsProperty();

    /**
     * Get the all artifacts.
     * 
     * @return A live item list.
     */
    default ObservableList<E> artifacts() {
        return refer().sorted.getValue();
    }

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
        if (items instanceof ObservableList) {
            return items((ObservableList<E>) items);
        }

        if (items == null || items.isEmpty()) {
            modifyItemUISafely(list -> {
                list.clear();
            });
        } else {
            if (this instanceof CollectableItemRenderingHelper && !items.isEmpty() && items.get(0) instanceof Translatable) {
                ((CollectableItemRenderingHelper<?, Translatable>) this).renderByVariable(Translatable::toTraslated);
            }

            modifyItemUISafely(list -> {
                list.setAll(items);
            });
        }
        return (Self) this;
    }

    /**
     * Sets all values as items.
     * 
     * @param items All items to set.
     * @return Chainable API.
     */
    default Self items(ObservableList<E> items) {
        Objects.requireNonNull(items);

        if (this instanceof CollectableItemRenderingHelper && !items.isEmpty() && items.get(0) instanceof Translatable) {
            ((CollectableItemRenderingHelper<?, Translatable>) this).renderByVariable(Translatable::toTraslated);
        }

        if (items != null) {
            modifyItemUISafely(list -> refer().items.setValue(items));
        }
        return (Self) this;
    }

    /**
     * Specify all values from the start value to the end value.
     *
     * @param start The inclusive initial value.
     * @param end The inclusive upper bound.
     * @param mapper A value builder.
     * @return Chainable API.
     */
    default Self items(int start, int end, IntFunction<E> mapper) {
        return items(IntStream.range(start, end + 1).mapToObj(mapper).collect(Collectors.toList()));
    }

    /**
     * Sets all values as items.
     * 
     * @param items
     * @return Chainable API.
     */
    default Self itemsWhen(Signal<?> reload, Supplier<List<E>> items) {
        return dispose(reload.startWithNull().to(() -> {
            List<E> list = items.get();
            if (list instanceof ObservableList o) {
                items(o);
            } else {
                items(list);
            }
        }));
    }

    /**
     * Sets all values as items.
     * 
     * @param setter
     * @return Chainable API.
     */
    default Self itemsWhen(Signal<?> reload, Consumer<Self> setter) {
        return dispose(reload.startWithNull().to(() -> setter.accept((Self) this)));
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialItems The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(E... initialItems) {
        return initialize(List.of(initialItems));
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialItems The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(Signal<E> initialItems) {
        return initialize(initialItems.toList());
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialItems The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(BaseStream<E, ?> initialItems) {
        return initialize(initialItems::iterator);
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialItems The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(Iterable<E> initialItems) {
        return initialize(I.signal(initialItems));
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialItems The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(List<E> initialItems) {
        return initialize(initialItems.isEmpty() ? null : initialItems.get(0), initialItems);
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param defaultValue A default value.
     * @param initialItems The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(E defaultValue, List<E> initialItems) {
        if (defaultValue != null && !initialItems.contains(defaultValue)) {
            initialItems.add(defaultValue);
        }

        items(initialItems);

        if (this instanceof ValueHelper && !initialItems.isEmpty()) {
            ((ValueHelper) this).initialize(defaultValue);
        }
        return (Self) this;
    }

    /**
     * Maintain an edit history of this value with default context.
     * 
     * @param save
     * @return
     */
    default Self historicalItems(WiseConsumer<List<E>> save) {
        return historicalItems(save, null);
    }

    /**
     * Maintains an edit history of this value with your context.
     * 
     * @param save
     * @return
     */
    default <X extends UserInterface & CollectableHelper<X, E>> Self historicalItems(WiseConsumer<List<E>> save, Edito context) {
        if (save != null) {
            if (context == null) context = Edito.Root;
            context.manageList((X) this, save);
        }
        return (Self) this;
    }

    /**
     * Returns the first item.
     * 
     * @return
     */
    default Variable<E> first() {
        ObservableList<E> items = items();

        if (items.isEmpty()) {
            return Variable.empty();
        } else {
            return Variable.of(items.get(0));
        }
    }

    /**
     * Returns the first item.
     * 
     * @return
     */
    default Variable<E> firstArtifact() {
        ObservableList<E> items = artifacts();

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
        ObservableList<E> items = items();

        if (items.isEmpty()) {
            return Variable.empty();
        } else {
            return Variable.of(items.get(items.size() - 1));
        }
    }

    /**
     * Returns the last item.
     * 
     * @return
     */
    default Variable<E> lastArtifact() {
        ObservableList<E> items = artifacts();

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
     * Return the index of the specified item or -1 if it is not found.
     * 
     * @param item A target to search.
     * @return An item index.
     */
    default int indexOf(E item) {
        return refer().items.getValue().indexOf(item);
    }

    /**
     * Check whether this collection has the specified item or not.
     * 
     * @param item A target to search.
     * @return A result.
     */
    default boolean has(E item) {
        return indexOf(item) != -1;
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
     * Add the specified item.
     * 
     * @param index An index to add.
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self addItemAtIfAbsent(int index, E item) {
        if (!has(item)) {
            addItemAt(index, item);
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
        modifyItemUISafely(list -> list.add(0, item));
        return (Self) this;
    }

    /**
     * Add the specified item at the first.
     * 
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self addItemAtFirstIfAbsent(E item) {
        if (!has(item)) {
            addItemAtFirst(item);
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
     * Add the specified item at the last.
     * 
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self addItemAtLastIfAbsent(E item) {
        if (!has(item)) {
            addItemAtLast(item);
        }
        return (Self) this;
    }

    /**
     * Set the specified item.
     * 
     * @param index An index to add.
     * @param item An item to add.
     * @return Chainable API.
     */
    default Self setItemAt(int index, E item) {
        if (item != null && 0 <= index) {
            modifyItemUISafely(list -> list.set(Math.min(index, list.size()), item));
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
     * Remove the specified item.
     * 
     * @param items A collection of items to remove.
     * @return Chainable API.
     */
    default Self removeItems(Collection<E> items) {
        modifyItemUISafely(list -> list.removeAll(items));
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
     * Remove an item at the specified index.
     * 
     * @return Chainable API.
     */
    default Self removeItemAt(int... indexies) {
        if (indexies.length != 0) {
            Arrays.sort(indexies);

            modifyItemUISafely(list -> {
                for (int i = indexies.length - 1; 0 <= i; i--) {
                    list.remove(indexies[i]);
                }
            });
        }
        return (Self) this;
    }

    /**
     * Remove an item at the specified index.
     * 
     * @return Chainable API.
     */
    default Self removeItemAt(List<Integer> indexies) {
        if (indexies != null && !indexies.isEmpty()) {
            removeItemAt(indexies.stream().mapToInt(Integer::intValue).toArray());
        }
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
     * Replace the specified item.
     * 
     * @param target An index..
     * @param replacer An item to replace.
     * @return Chainable API.
     */
    default Self replaceItemAt(Variable<E> target, E replacer) {
        return replaceItemAt(target.v, replacer);
    }

    /**
     * Replace the specified item.
     * 
     * @param target An index..
     * @param replacer An item to replace.
     * @return Chainable API.
     */
    default Self replaceItemAt(E target, E replacer) {
        if (target != null && replacer != null) {
            modifyItemUISafely(list -> {
                int index = list.indexOf(target);
                if (index != -1) {
                    list.set(index, replacer);
                }
            });
        }
        return (Self) this;
    }

    /**
     * Swap its position each items.
     * 
     * @param one
     * @param other
     * @return Chainable API.
     */
    default Self swap(int one, int other) {
        modifyItemUISafely(list -> {
            Collections.swap(list, one, other);
        });
        return (Self) this;
    }

    /**
     * Modify items in UI thread.
     * 
     * @param action
     */
    private void modifyItemUISafely(Consumer<ObservableList<E>> action) {
        Viewtify.inUI(() -> action.accept(items()));
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
     * Sort items by the specified {@link Comparator}.
     * 
     * @param sorter A item comparator.
     * @return Chainable API.
     */
    default <U extends Comparable<? super U>> Self sortBy(Function<? super E, ? extends U> sorter) {
        return sort(Comparator.comparing(sorter));
    }

    /**
     * Sort items by the specified {@link Comparator}.
     * 
     * @param sorter A item comparator.
     * @return Chainable API.
     */
    default Self sortByInt(ToIntFunction<? super E> sorter) {
        return sort(Comparator.comparingInt(sorter));
    }

    /**
     * Sort items by the specified {@link Comparator}.
     * 
     * @param sorter A item comparator.
     * @return Chainable API.
     */
    default Self sortByLong(ToLongFunction<? super E> sorter) {
        return sort(Comparator.comparingLong(sorter));
    }

    /**
     * Sort items by the specified {@link Comparator}.
     * 
     * @param sorter A item comparator.
     * @return Chainable API.
     */
    default Self sortByDouble(ToDoubleFunction<? super E> sorter) {
        return sort(Comparator.comparingDouble(sorter));
    }

    /**
     * Sort items by the specified {@link Comparator}.
     * 
     * @param context An additional infomation.
     * @param sorter A item comparator.
     * @return Chainable API.
     */
    default <V> Self sort(ValueHelper<?, V> context, Function<V, Comparator<E>> sorter) {
        if (context != null && sorter != null) {
            context.observing().to(v -> sort(sorter.apply(v)));
        }
        return (Self) this;
    }

    /**
     * Sorts items using the specified {@link Comparator}. However, items other than the selected
     * item are excluded from the sort and remain fixed.
     * 
     * @param sorter A item comparator.
     * @param selector A item selector to sort.
     */
    default Self sort(Comparator<E> sorter, Predicate<E> selector) {
        ObservableList<E> items = items();

        List<E> sorted = I.signal(items).take(selector).sort(sorter).toList();
        I.signal(items).skip(selector).to(item -> {
            int index = items.indexOf(item);
            sorted.add(index, item);
        });

        return items(sorted);
    }

    /**
     * Get the filetering state.
     * 
     * @return
     */
    default Signal<Boolean> isFiltering() {
        return refer().filtering.expose.diff();
    }

    /**
     * Get the filetering state.
     * 
     * @return
     */
    default Signal<Boolean> hasNothing() {
        return Viewtify.observe(items()).map(ObservableList::isEmpty);
    }

    /**
     * Get the filetering state.
     * 
     * @return
     */
    default Signal<Boolean> hasSomething() {
        return Viewtify.observe(items()).map(v -> !v.isEmpty());
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
    default Self take(Signal<Predicate<E>> filter) {
        return dispose(filter.to(this::take));
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
     * Get the associated {@link CompoundQuery}.
     * 
     * @return
     */
    default CompoundQuery<E> query() {
        return refer().query();
    }

    /**
     * Observe each items's state to update view.
     * 
     * @param stateExtractor
     * @return Chainable API.
     */
    default Self observeItemState(Function<E, Variable> stateExtractor) {
        Ð<E> refer = refer();
        if (refer.disposers != null) {
            refer.disposers.values().forEach(Disposable::dispose);
            refer.disposers.clear();
        }
        refer.disposers = new WeakHashMap();
        refer.notifier = stateExtractor;
        return (Self) this;
    }

    /**
     * Observe the modification of items.
     * 
     * @return
     */
    default Signal<ObservableList<E>> observeItems() {
        return Viewtify.observe(refer().items).switchMap(Viewtify::observing);
    }

    /**
     * Observe the modification of items.
     * 
     * @return
     */
    default Self observeItems(WiseConsumer<ObservableList<E>> listener) {
        return dispose(observeItems().to(listener));
    }

    /**
     * Observe the modification of items.
     * 
     * @return
     */
    default Signal<ObservableList<E>> observingItems() {
        return Viewtify.observing(refer().items).switchMap(Viewtify::observing);
    }

    /**
     * Observe the modification of items.
     * 
     * @return
     */
    default Self observingItems(WiseConsumer<ObservableList<E>> listener) {
        return dispose(observingItems().to(listener));
    }

    /**
     * Observe the modification of artifacts.
     * 
     * @return
     */
    default Signal<ObservableList<E>> observeArtifacts() {
        return Viewtify.observe(refer().sorted).switchMap(Viewtify::observing);
    }

    /**
     * Observe the modification of artifacts.
     * 
     * @return
     */
    default Self observeArtifacts(WiseConsumer<ObservableList<E>> listener) {
        return dispose(observeArtifacts().to(listener));
    }

    /**
     * Observe the modification of artifacts.
     * 
     * @return
     */
    default Signal<ObservableList<E>> observingArtifacts() {
        return Viewtify.observing(refer().sorted).switchMap(Viewtify::observing);
    }

    /**
     * Observe the modification of artifacts.
     * 
     * @return
     */
    default Self observingArtifacts(WiseConsumer<ObservableList<E>> listener) {
        return dispose(observingArtifacts().to(listener));
    }

    /**
     * Reapply the current filter and comparator.
     * 
     * @return Chainable API.
     */
    default Self reapply() {
        Ð<E> refer = refer();
        if (refer.filter.isPresent()) {
            refer.invokeRefilter();
        }
        if (refer.sorter.isPresent()) {
            refer.invokeResort();
        }
        return (Self) this;
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
    final class Ð<E> implements ListChangeListener<E> {

        /** The internal method accessor. */
        private static final Method refilter;

        /** The internal method accessor. */
        private static final Method resort;

        // cache the internal method reference
        static {
            try {
                refilter = FilteredList.class.getDeclaredMethod("refilter");
                refilter.setAccessible(true);
                resort = SortedList.class.getDeclaredMethod("doSortWithPermutationChange");
                resort.setAccessible(true);
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }

        /** The item holder. */
        private final Property<ObservableList<E>> items = new SmartProperty();

        /** The intermediate product. */
        private FilteredList<E> filtered;

        /** The artifact holder. */
        private final Property<SortedList<E>> sorted = new SmartProperty();

        /** The filtered state. */
        private final Signaling<Boolean> filtering = new Signaling();

        /** The item taking filter. */
        private final Variable<Predicate<E>> filter = Variable.empty();

        /** The item sorter. */
        private final Variable<Comparator<E>> sorter = Variable.empty();

        /** The item state observers. */
        private Function<E, Variable> notifier;

        /** The disposer for observers. */
        private WeakHashMap<E, Disposable> disposers;

        /** The sync state. */
        private final GuardedOperation updating = new GuardedOperation();

        /** Lazy initialization. */
        private volatile CompoundQuery<E> query;

        /**
         * Initialize date reference.
         * 
         * @param helper
         */
        private Ð(CollectableHelper<?, E> helper) {
            ObservableList<E> list = helper.itemsProperty().getValue();
            if (list != null) {
                items.setValue(list);
                list.addListener(this);
            }

            Viewtify.observing(items).skipNull().to(v -> {
                updating.guard(() -> {
                    filtered = new FilteredList(v, filter.v);
                    sorted.setValue(new SortedList(filtered, sorter.v));

                    helper.itemsProperty().setValue(sorted.getValue());
                });
            });

            filter.observe().to(v -> {
                if (filtered != null) {
                    if (filtered.getPredicate() == v) {
                        invokeRefilter();
                    } else {
                        filtered.setPredicate(v);
                    }
                }
            });
            sorter.observe().to(v -> {
                if (sorted.getValue() != null) sorted.getValue().setComparator(v);
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChanged(Change<? extends E> c) {
            if (notifier != null) {
                while (c.next()) {
                    for (E item : c.getRemoved()) {
                        Disposable disposable = disposers.remove(item);
                        if (disposable != null) {
                            disposable.dispose();
                        }
                    }
                    for (E item : c.getAddedSubList()) {
                        disposers.put(item, notifier.apply(item).observe().to(() -> {
                            // Dirty Hack : notify item change event to the source observable list
                            ObservableList<E> list = items.getValue();
                            list.set(list.indexOf(item), item);
                        }));
                    }
                }
            }
        }

        /**
         * Get the associated {@link CompoundQuery} lazily.
         * 
         * @return
         */
        private CompoundQuery<E> query() {
            if (query == null) {
                synchronized (this) {
                    if (query == null) {
                        query = new CompoundQuery();
                        query.updated.to(filter::set);
                    }
                }
            }
            return query;
        }

        /**
         * Invoke the internal refilter method.
         */
        private void invokeRefilter() {
            try {
                refilter.invoke(filtered);
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }

        /**
         * Invoke the internal resort method.
         */
        private void invokeResort() {
            try {
                resort.invoke(sorted.getValue());
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }
    }
}