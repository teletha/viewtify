/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.util.HashMap;
import java.util.List;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import org.junit.jupiter.api.Test;

import kiss.Variable;

class ViewtifyTest {

    @Test
    void observeList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        List<ObservableList<String>> result = Viewtify.observe(list).toList();
        list.add("one");

        assert result.size() == 1;
        assert result.get(0) == list;
    }

    @Test
    void observeSet() {
        ObservableSet<String> set = FXCollections.observableSet();
        List<ObservableSet<String>> result = Viewtify.observe(set).toList();
        set.add("one");

        assert result.size() == 1;
        assert result.get(0) == set;
    }

    @Test
    void observeMap() {
        ObservableMap<String, String> map = FXCollections.observableMap(new HashMap());
        List<ObservableMap<String, String>> result = Viewtify.observe(map).toList();
        map.put("key", "value");

        assert result.size() == 1;
        assert result.get(0) == map;
    }

    @Test
    void propertyBoolean() {
        Variable<Boolean> variable = Variable.of(true);

        Property<Boolean> proeprty = Viewtify.property(variable);
        assert proeprty.getValue() == true;

        variable.set(false);
        assert proeprty.getValue() == false;

        proeprty.setValue(true);
        assert variable.v == true;
    }

    @Test
    void propertyInt() {
        Variable<Integer> variable = Variable.of(1);

        Property<Integer> proeprty = Viewtify.property(variable);
        assert proeprty.getValue() == 1;

        variable.set(2);
        assert proeprty.getValue() == 2;

        proeprty.setValue(3);
        assert variable.v == 3;
    }

    @Test
    void propertyBind() {
        Variable<String> variable = Variable.of("var");
        Property<String> property = Viewtify.property(variable);

        SimpleStringProperty target = new SimpleStringProperty();
        assert target.get() == null;

        target.bind(property);
        assert target.get().equals("var");

        variable.set("from variable");
        assert target.get().equals("from variable");
    }

    @Test
    void propertyBindMultiple() {
        Variable<String> variable1 = Variable.of("var1");
        Property<String> property1 = Viewtify.property(variable1);

        Variable<String> variable2 = Variable.of("var2");
        Property<String> property2 = Viewtify.property(variable2);

        SimpleStringProperty target = new SimpleStringProperty();

        target.bind(property1);
        assert target.get().equals("var1");

        target.bind(property2);
        assert target.get().equals("var2");

        variable1.set("change from old binding");
        assert target.get().equals("var2");

        variable2.set("change from new binding");
        assert target.get().equals("change from new binding");
    }
}