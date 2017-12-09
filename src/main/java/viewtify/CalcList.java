/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Variable;

/**
 * @version 2017/12/09 13:14:23
 */
public class CalcList<E> extends ListBinding<E> {

    private final String name;

    /** The element observer. */
    private final InvalidationListener forElement = o -> invalidate();

    /** The list observer. */
    private final ListChangeListener<E> forList = this::onChanged;

    /** The source list. */
    private final ObservableList<E> list;

    /** The selector list. */
    private List<Function<E, ? extends Observable>> selectors;

    /**
     * @param list
     */
    CalcList(String name, ObservableList<E> list) {
        this.name = name;
        this.list = list;

        list.addListener(forList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        list.removeListener(forList);

        if (selectors != null) {
            forEach(e -> selectors.forEach(s -> unbind(s.apply(e))));

            selectors.clear();
            selectors = null;
        }
    }

    /**
     * Called after a change has been made to an ObservableList.
     *
     * @param change an object representing the change that was done
     */
    private void onChanged(Change<? extends E> change) {
        while (change.next()) {
            System.out.println(change);
            if (selectors != null) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(e -> selectors.forEach(s -> unbind(s.apply(e))));
                }

                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(e -> selectors.forEach(s -> bind(s.apply(e))));
                }
            }
            if (isValid()) {
                invalidate();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInvalidating() {
        List<String> callers = I.signal(new Error().getStackTrace())
                .skip(1)
                .take(e -> e.getClassName().startsWith("viewtify."))
                .map(caller -> "\t\t at " + caller.getClassName() + "." + caller.getMethodName() + "(" + caller.getFileName() + ":" + caller
                        .getLineNumber() + ")")
                .toList();

        System.out.println(this + "  is  invalidate now.");
        callers.forEach(System.out::println);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObservableList<E> computeValue() {
        return list;
    }

    /**
     * Register {@link Observable} property selector.
     * 
     * @param selector
     * @return
     */
    public final synchronized CalcList<E> checkObservable(Function<E, ? extends Observable> selector) {
        if (selector != null) {
            if (selectors == null) {
                selectors = new CopyOnWriteArrayList<>();
            }

            if (!selectors.contains(selector)) {
                selectors.add(selector);

                forEach(e -> bind(selector.apply(e)));
            }
        }
        return this;
    }

    /**
     * Register {@link Variable} property selector.
     * 
     * @param selector
     * @return
     */
    public final CalcList<E> checkVariable(Function<E, ? extends Variable> selector) {
        if (selector == null) {
            return this;
        } else {
            return checkObservable(e -> Viewtify.calculate(selector.apply(e)));
        }
    }

    /**
     * Create mapped {@link CalcList} for {@link ObservableValue}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalcList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        checkObservable(mapper);
        return calculate("flatObservable", new MappedList<>(this, e -> mapper.apply(e).getValue()));
    }

    /**
     * Create mapped {@link CalcList} for {@link Variable}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalcList<R> flatVariable(Function<E, Variable<R>> mapper) {
        checkVariable(mapper);
        return calculate("flatVariable", new MappedList<>(this, e -> mapper.apply(e).get()));
    }

    /**
     * Create mapped {@link CalcList}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalcList<R> map(Function<E, R> mapper) {
        return calculate("map", new MappedList<>(this, mapper));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CalcList[" + name + "]";
    }

    public static <E> CalcList<E> calculate(ObservableList<E> list) {
        return list instanceof CalcList ? (CalcList) list : new CalcList("root", list);
    }

    public static <E> CalcList<E> calculate(String name, ObservableList<E> list) {
        return list instanceof CalcList ? (CalcList) list : new CalcList(name, list);
    }
}
