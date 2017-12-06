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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Test;

import kiss.Variable;
import viewtify.Calculatable;
import viewtify.CalculatableList;
import viewtify.Viewtify;

/**
 * @version 2017/12/05 23:45:03
 */
public class CalculatableListTest {

    @Test
    public void map() throws Exception {
        ObservableList<String> source = FXCollections.observableArrayList("one", "two", "three");
        CalculatableList<String> result = Viewtify.calculate(source).map(v -> v.toUpperCase());
        assert result.size() == 3;
        assert result.get().get(0).equals("ONE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");

        source.add("four");
        assert result.size() == 4;
        assert result.get().get(0).equals("ONE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");
    }

    @Test
    public void flatObservable() throws Exception {
        Value<String> v1 = Value.of("one");
        Value<String> v2 = Value.of("two");
        Value<String> v3 = Value.of("three");
        ObservableList<Value<String>> source = FXCollections.observableArrayList(v1, v2, v3);
        CalculatableList<String> result = Viewtify.calculate(source).flatObservable(v -> v.property);
        assert result.size() == 3;
        assert result.get().get(0).equals("one");
        assert result.get().get(1).equals("two");
        assert result.get().get(2).equals("three");

        // add to source list
        Value<String> v4 = Value.of("four");
        source.add(v4);
        assert result.size() == 4;
        assert result.get().get(0).equals("one");
        assert result.get().get(1).equals("two");
        assert result.get().get(2).equals("three");
        assert result.get().get(3).equals("four");

        // change on source list
        source.set(0, Value.of("change"));
        assert result.size() == 4;
        assert result.get().get(0).equals("change");
        assert result.get().get(1).equals("two");
        assert result.get().get(2).equals("three");
        assert result.get().get(3).equals("four");

        // change on source item
        v2.property.set("TWO");
        assert result.size() == 4;
        assert result.get().get(0).equals("change");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("three");
        assert result.get().get(3).equals("four");
    }

    @Test
    public void flatObservableAndMap() throws Exception {
        Value<String> v1 = Value.of("one");
        Value<String> v2 = Value.of("two");
        Value<String> v3 = Value.of("three");
        ObservableList<Value<String>> source = FXCollections.observableArrayList(v1, v2, v3);
        CalculatableList<String> result = Viewtify.calculate(source).flatObservable(v -> v.property).map(String::toUpperCase);
        assert result.size() == 3;
        assert result.get().get(0).equals("ONE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");

        // add to source list
        Value<String> v4 = Value.of("four");
        source.add(v4);
        assert result.size() == 4;
        assert result.get().get(0).equals("ONE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");

        // change on source list
        source.set(0, Value.of("change"));
        assert result.size() == 4;
        assert result.get().get(0).equals("CHANGE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");

        // change on source item
        v2.property.set("property");
        assert result.size() == 4;
        assert result.get().get(0).equals("CHANGE");
        assert result.get().get(1).equals("PROPERTY");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");
    }

    @Test
    public void flatObservableAndReduce() throws Exception {
        Value<Integer> v1 = Value.of(1);
        Value<Integer> v2 = Value.of(2);
        Value<Integer> v3 = Value.of(3);
        ObservableList<Value<Integer>> source = FXCollections.observableArrayList(v1, v2, v3);
        Calculatable<Integer> result = Viewtify.calculate(source).flatObservable(v -> v.property).reduce(0, (p, q) -> p + q);
        assert result.get() == 6;

        // add to source list
        Value<Integer> v4 = Value.of(4);
        source.add(v4);
        assert result.get() == 10;

        // change on source list
        source.set(0, Value.of(10));
        assert result.get() == 19;

        // change on source item
        v2.property.set(5);
        assert result.get() == 22;
    }

    @Test
    public void flatVariable() throws Exception {
        Value<String> v1 = Value.of("one");
        Value<String> v2 = Value.of("two");
        Value<String> v3 = Value.of("three");
        ObservableList<Value<String>> source = FXCollections.observableArrayList(v1, v2, v3);
        CalculatableList<String> result = Viewtify.calculate(source).flatVariable(v -> v.variable);
        assert result.size() == 3;
        assert result.get().get(0).equals("one");
        assert result.get().get(1).equals("two");
        assert result.get().get(2).equals("three");

        // add to source list
        Value<String> v4 = Value.of("four");
        source.add(v4);
        assert result.size() == 4;
        assert result.get().get(0).equals("one");
        assert result.get().get(1).equals("two");
        assert result.get().get(2).equals("three");
        assert result.get().get(3).equals("four");

        // change on source list
        source.set(0, Value.of("change"));
        assert result.size() == 4;
        assert result.get().get(0).equals("change");
        assert result.get().get(1).equals("two");
        assert result.get().get(2).equals("three");
        assert result.get().get(3).equals("four");

        // change on source item
        v2.variable.set("variable");
        assert result.size() == 4;
        assert result.get().get(0).equals("change");
        assert result.get().get(1).equals("variable");
        assert result.get().get(2).equals("three");
        assert result.get().get(3).equals("four");
    }

    @Test
    public void flatVariableAndMap() throws Exception {
        Value<String> v1 = Value.of("one");
        Value<String> v2 = Value.of("two");
        Value<String> v3 = Value.of("three");
        ObservableList<Value<String>> source = FXCollections.observableArrayList(v1, v2, v3);
        CalculatableList<String> result = Viewtify.calculate(source).flatVariable(v -> v.variable).map(String::toUpperCase);
        assert result.size() == 3;
        assert result.get().get(0).equals("ONE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");

        // add to source list
        Value<String> v4 = Value.of("four");
        source.add(v4);
        assert result.size() == 4;
        assert result.get().get(0).equals("ONE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");

        // change on source list
        source.set(0, Value.of("change"));
        assert result.size() == 4;
        assert result.get().get(0).equals("CHANGE");
        assert result.get().get(1).equals("TWO");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");

        // change on source item
        v2.variable.set("variable");
        assert result.size() == 4;
        assert result.get().get(0).equals("CHANGE");
        assert result.get().get(1).equals("VARIABLE");
        assert result.get().get(2).equals("THREE");
        assert result.get().get(3).equals("FOUR");
    }

    @Test
    public void flatVariableAndReduce() throws Exception {
        Value<Integer> v1 = Value.of(1);
        Value<Integer> v2 = Value.of(2);
        Value<Integer> v3 = Value.of(3);
        ObservableList<Value<Integer>> source = FXCollections.observableArrayList(v1, v2, v3);
        Calculatable<Integer> result = Viewtify.calculate(source).flatVariable(v -> v.variable).reduce(0, (p, q) -> p + q);
        assert result.get() == 6;

        // add to source list
        Value<Integer> v4 = Value.of(4);
        source.add(v4);
        assert result.get() == 10;
        assert result.get() == 10;
        assert result.get() == 10;

        // change on source list
        source.set(0, Value.of(10));
        assert result.get() == 19;

        // change on source item
        v2.variable.set(5);
        assert result.get() == 22;
    }

    /**
     * @version 2017/12/06 1:26:43
     */
    private static class Value<T> {

        public final Variable<T> variable = Variable.empty();

        public final ObjectProperty<T> property = new SimpleObjectProperty();

        /**
         * Create new Value<String>.
         * 
         * @param Value<String>
         * @return
         */
        public static <T> Value<T> of(T value) {
            Value<T> v = new Value<T>();
            v.variable.set(value);
            v.property.set(value);

            return v;
        }
    }
}
