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

import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.junit.Test;

import kiss.I;
import kiss.Variable;

/**
 * @version 2017/12/06 23:28:16
 */
public class CalculationTest {

    @Test
    public void calculationFromObservableValue() throws Exception {
        ObjectProperty<Integer> property = new SimpleObjectProperty(10);

        Calculation<Integer> calculation = Viewtify.calculate(property);
        assert calculation.isValid() == false;
        assert calculation.get() == 10;
        assert calculation.isValid() == true;

        property.set(20);
        assert calculation.isValid() == false;
        assert calculation.get() == 20;
        assert calculation.isValid() == true;

        property.set((Integer) null);
        assert calculation.isValid() == false;
        assert calculation.get() == null;
        assert calculation.isValid() == true;
    }

    @Test
    public void calculationFromVariable() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Integer> calculation = Viewtify.calculate(variable);
        assert calculation.isValid() == false;
        assert calculation.get() == 10;
        assert calculation.isValid() == true;

        variable.set(20);
        assert calculation.isValid() == false;
        assert calculation.get() == 20;
        assert calculation.isValid() == true;

        variable.set((Integer) null);
        assert calculation.isValid() == false;
        assert calculation.get() == null;
        assert calculation.isValid() == true;
    }

    @Test
    public void as() throws Exception {
        Integer value = Integer.valueOf(10);
        Property<Object> object = new SimpleObjectProperty(10);
        Calculation<Object> calculation = Viewtify.calculate(object);
        assert calculation.as(Integer.class).get() == 10;
        assert calculation.as(Number.class).get().equals(value);
        assert calculation.as(Object.class).get().equals(value);

        assert calculation.as(String.class).get() == null;
        assert calculation.as(Map.class).get() == null;
    }

    @Test(expected = NullPointerException.class)
    public void asNull() throws Exception {
        Viewtify.calculate(Variable.of(10)).as(null);
    }

    @Test
    public void is() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Boolean> calculation = Viewtify.calculate(variable).is(10, 20);
        assert calculation.get() == true;
        assert calculation.isValid() == true;

        variable.set(15);
        assert calculation.isValid() == false;
        assert calculation.get() == false;
        assert calculation.isValid() == true;

        variable.set(20);
        assert calculation.isValid() == false;
        assert calculation.get() == true;
        assert calculation.isValid() == true;
    }

    @Test
    public void isSet() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Boolean> calculation = Viewtify.calculate(variable).is(I.set(10, 20));
        assert calculation.get() == true;
        assert calculation.isValid() == true;

        variable.set(15);
        assert calculation.isValid() == false;
        assert calculation.get() == false;
        assert calculation.isValid() == true;

        variable.set(20);
        assert calculation.isValid() == false;
        assert calculation.get() == true;
        assert calculation.isValid() == true;
    }

    @Test
    public void isNot() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Boolean> calculation = Viewtify.calculate(variable).isNot(10, 20);
        assert calculation.get() == false;
        assert calculation.isValid() == true;

        variable.set(15);
        assert calculation.isValid() == false;
        assert calculation.get() == true;
        assert calculation.isValid() == true;

        variable.set(20);
        assert calculation.isValid() == false;
        assert calculation.get() == false;
        assert calculation.isValid() == true;
    }

    @Test
    public void isNotSet() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Boolean> calculation = Viewtify.calculate(variable).isNot(I.set(10, 20));
        assert calculation.get() == false;
        assert calculation.isValid() == true;

        variable.set(15);
        assert calculation.isValid() == false;
        assert calculation.get() == true;
        assert calculation.isValid() == true;

        variable.set(20);
        assert calculation.isValid() == false;
        assert calculation.get() == false;
        assert calculation.isValid() == true;
    }

    @Test
    public void isAbsent() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Boolean> calculation = Viewtify.calculate(variable).isAbsent();
        assert calculation.get() == false;
        assert calculation.isValid() == true;

        variable.set((Integer) null);
        assert calculation.isValid() == false;
        assert calculation.get() == true;
        assert calculation.isValid() == true;
    }

    @Test
    public void isPresent() throws Exception {
        Variable<Integer> variable = Variable.of(10);

        Calculation<Boolean> calculation = Viewtify.calculate(variable).isPresent();
        assert calculation.get() == true;
        assert calculation.isValid() == true;

        variable.set((Integer) null);
        assert calculation.isValid() == false;
        assert calculation.get() == false;
        assert calculation.isValid() == true;
    }

    @Test
    public void or() throws Exception {
        StringProperty p = new SimpleStringProperty("TEST");
        Calculation<String> calc = Viewtify.calculate(p).or("OTHER");
        assert calc.get().equals("TEST");

        p.set(null);
        assert calc.get().equals("OTHER");
    }

    @Test
    public void orObservable() throws Exception {
        StringProperty p1 = new SimpleStringProperty("TEST");
        StringProperty p2 = new SimpleStringProperty("OTHER");

        Calculation<String> calc = Viewtify.calculate(p1).or(p2);
        assert calc.get().equals("TEST");

        p1.set(null);
        assert calc.get().equals("OTHER");

        p2.set("CHANGE");
        assert calc.get().equals("CHANGE");

        p1.set("REVERT");
        assert calc.get().equals("REVERT");
    }

    @Test
    public void orVariable() throws Exception {
        Variable<String> p1 = Variable.of("TEST");
        Variable<String> p2 = Variable.of("OTHER");

        Calculation<String> calc = Viewtify.calculate(p1).or(p2);
        assert calc.get().equals("TEST");

        p1.set((String) null);
        assert calc.get().equals("OTHER");

        p2.set("CHANGE");
        assert calc.get().equals("CHANGE");

        p1.set("REVERT");
        assert calc.get().equals("REVERT");
    }

    @Test
    public void skip() throws Exception {
        StringProperty p = new SimpleStringProperty("TEST");
        Calculation<String> calculation = Viewtify.calculate(p).skip(v -> v.contains("S"));
        assert calculation.get() == null;
        assert calculation.isValid() == true;

        p.set("OK");
        assert calculation.isValid() == false;
        assert calculation.get().equals("OK");
        assert calculation.isValid() == true;
    }

    @Test
    public void take() throws Exception {
        StringProperty p = new SimpleStringProperty("TEST");
        Calculation<String> calculation = Viewtify.calculate(p).take(v -> v.contains("S"));
        assert calculation.get().equals("TEST");
        assert calculation.isValid() == true;

        p.set("NOOO");
        assert calculation.isValid() == false;
        assert calculation.get() == null;
        assert calculation.isValid() == true;
    }

    @Test
    public void map() throws Exception {
        StringProperty p = new SimpleStringProperty("TEST");
        Calculation<String> calc = Viewtify.calculate(p).map(String::toLowerCase);
        assert calc.get().equals("test");

        p.set("CHANGE");
        assert calc.get().equals("change");

        p.set(null);
        assert calc.get() == null;
    }

    @Test
    public void calculateProperty() throws Exception {
        Nest nest = new Nest("TEST");
        ObjectProperty<Nest> p = new SimpleObjectProperty(nest);
        Calculation<String> calc = Viewtify.calculate(p).calculateProperty(v -> v.property);
        assert calc.get().equals("TEST");

        // change inner value
        nest.property.set("CHANGE");
        assert calc.get().equals("CHANGE");

        // inner null
        nest.property.set(null);
        assert calc.get() == null;

        // change outer value
        p.set(new Nest("OUTER"));
        assert calc.get().equals("OUTER");

        // outer null
        p.set(null);
        assert calc.get() == null;
    }

    @Test
    public void calculateVariable() throws Exception {
        Nest nest = new Nest("TEST");
        ObjectProperty<Nest> p = new SimpleObjectProperty(nest);
        Calculation<String> calc = Viewtify.calculate(p).calculateVariable(v -> v.variable);
        assert calc.get().equals("TEST");

        // change inner value
        nest.variable.set("CHANGE");
        assert calc.get().equals("CHANGE");

        // inner null
        nest.variable.set((String) null);
        assert calc.get() == null;

        // change outer value
        p.set(new Nest("OUTER"));
        assert calc.get().equals("OUTER");

        // outer null
        p.set(null);
        assert calc.get() == null;
    }

    /**
     * @version 2017/12/03 17:57:45
     */
    private static class Nest {

        private StringProperty property = new SimpleStringProperty();

        private Variable<String> variable = Variable.empty();

        private Nest(String text) {
            this.property.set(text);
            this.variable.set(text);
        }
    }
}
