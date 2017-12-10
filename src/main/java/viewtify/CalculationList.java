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

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Variable;
import kiss.WiseBiFunction;

/**
 * @version 2017/12/09 13:14:23
 */
public class CalculationList<E> extends BindingBase<ObservableList<E>> {

    private final String name;

    /** The source list. */
    private final ObservableList<E> list;

    /** The selector list. */
    private List<Function<E, ? extends Observable>> observableSelectors;

    /** The item change observer. */
    private final ListChangeListener<E> itemChangeListener = change -> {
        while (change.next()) {
            if (observableSelectors != null) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(e -> observableSelectors.forEach(s -> independFrom(s.apply(e))));
                }

                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(e -> observableSelectors.forEach(s -> dependOn(s.apply(e))));
                }
            }
        }
    };

    /**
     * @param list
     */
    CalculationList(String name, ObservableList<E> list, Observable... dependencies) {
        this.name = name;
        this.list = list;

        dependOn(list);

        for (Observable dependnecy : dependencies) {
            dependOn(dependnecy);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObservableList<E> computeValue() {
        System.out.println(list);
        return list;
    }

    /**
     * Register {@link Observable} property selector.
     * 
     * @param selector
     * @return
     */
    public final synchronized CalculationList<E> checkObservable(Function<E, ? extends Observable> selector) {
        if (selector != null) {
            if (observableSelectors == null) {
                observableSelectors = new CopyOnWriteArrayList<>();
                list.addListener(itemChangeListener);
            }

            if (!observableSelectors.contains(selector)) {
                observableSelectors.add(selector);
                list.forEach(e -> {
                    dependOn(selector.apply(e));
                });
            }
        }
        return this;
    }

    /**
     * Create mapped {@link CalculationList} for {@link ObservableValue}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        checkObservable(mapper);
        return Viewtify.calculate("flatObservable", new MappedList<>(this, e -> mapper.apply(e).getValue()), this);
    }

    /**
     * Create mapped {@link CalculationList} for {@link Variable}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> flatVariable(Function<E, Variable<R>> mapper) {
        checkObservable(mapper);
        return Viewtify.calculate("flatVariable", new MappedList<>(this, e -> {
            return mapper.apply(e).get();
        }));
    }

    public Calculation<Boolean> isNot(E value) {
        return new Calculation<Boolean>(() -> {
            return !getValue().contains(value);
        }, null, this);
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    public Calculation<E> item(int index) {
        return Viewtify.calculate(this, () -> {
            ObservableList<E> value = getValue();
            return index < value.size() ? value.get(index) : null;
        });
    }

    /**
     * Create mapped {@link CalculationList}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> map(Function<E, R> mapper) {
        return Viewtify.calculate("map", new MappedList<>(this, mapper));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    public <R> Calculation<R> reduce(R init, WiseBiFunction<R, E, R> accumulator) {
        return new Calculation<R>(() -> {
            return I.signal(getValue()).scan(init, accumulator).to().v;
        }, null, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CalcList[" + name + "]";
    }
}
