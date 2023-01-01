/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.time.LocalDate;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import kiss.Disposable;
import kiss.Variable;
import kiss.WiseFunction;

class ValueHelperTest {

    @Test
    void value() {
        StringValue v = new StringValue();
        assert v.value() == null;

        v.value("updated");
        assert v.value().equals("updated");
    }

    @Test
    void valueAs() {
        StringValue v = new StringValue("123");
        assert v.valueAs(Integer.class) == 123;
        assert v.valueAs(int.class) == 123;
        assert v.valueAs(boolean.class) == false;
        assert v.valueAs(double.class) == 123d;

        Assertions.assertThrows(ClassNotFoundException.class, () -> v.valueAs(Class.class));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> v.valueAs(ValueHelper.class));
    }

    @Test
    void valueOr() {
        StringValue v = new StringValue("123");
        // convertable value
        assert v.valueOr("not used").equals("123");
        assert v.valueOr(987) == 123;
        assert v.valueOr(true) == false;
        assert v.valueOr(987d) == 123d;

        // unconvertable value
        LocalDate date = LocalDate.now();
        assert v.valueOr(date) == date;
    }

    @Test
    void valueOrVariable() {
        StringValue v = new StringValue("123");
        // convertable value
        assert v.valueOr("not used").equals("123");
        assert v.valueOr(Variable.of(987)) == 123;
        assert v.valueOr(Variable.of(true)) == false;
        assert v.valueOr(Variable.of(987d)) == 123d;

        // unconvertable value
        LocalDate date = LocalDate.now();
        assert v.valueOr(Variable.of(date)) == date;
    }

    @Test
    void set() {
        StringValue v = new StringValue("123");
        v.value("updated");
        assert v.value().equals("updated");

        v.value((String) null);
        assert v.value() == null;
    }

    @Test
    void setWithCurrentValue() {
        StringValue v = new StringValue("123");
        v.value(current -> current + 456);
        assert v.value().equals("123456");

        v.value((WiseFunction) null);
        assert v.value().equals("123456");

        v.value(current -> null);
        assert v.value() == null;
    }

    @Test
    void syncProperty() {
        StringValue v = new StringValue("123");
        StringProperty p = new SimpleStringProperty("property");
        Disposable unsync = Disposable.empty();

        v.sync(p, unsync);
        assert v.value().equals("property");
        assert p.getValue().equals("property");

        // from value
        v.value("change on value");
        assert v.value().equals("change on value");
        assert p.getValue().equals("change on value");

        // from property
        p.setValue("change on property");
        assert v.value().equals("change on property");
        assert p.getValue().equals("change on property");

        // unsynchronize
        unsync.dispose();

        // from value
        v.value("change value only");
        assert v.value().equals("change value only");
        assert p.getValue().equals("change on property");

        // from property
        p.setValue("change property only");
        assert v.value().equals("change value only");
        assert p.getValue().equals("change property only");
    }

    @Test
    void syncProperties() {
        StringValue v = new StringValue("123");
        StringProperty p1 = new SimpleStringProperty("property1");
        StringProperty p2 = new SimpleStringProperty("property2");
        Disposable unsync1 = Disposable.empty();
        Disposable unsync2 = Disposable.empty();

        v.sync(p1, unsync1).sync(p2, unsync2);
        assert v.value().equals("property2");
        assert p1.getValue().equals("property2");
        assert p2.getValue().equals("property2");

        // from value
        v.value("change on value");
        assert v.value().equals("change on value");
        assert p1.getValue().equals("change on value");
        assert p2.getValue().equals("change on value");

        // from property1
        p1.setValue("change on property1");
        assert v.value().equals("change on property1");
        assert p1.getValue().equals("change on property1");
        assert p2.getValue().equals("change on property1");

        // from property2
        p2.setValue("change on property2");
        assert v.value().equals("change on property2");
        assert p1.getValue().equals("change on property2");
        assert p2.getValue().equals("change on property2");

        // unsynchronize1
        unsync1.dispose();

        // from value
        v.value("change value and property2");
        assert v.value().equals("change value and property2");
        assert p1.getValue().equals("change on property2");
        assert p2.getValue().equals("change value and property2");

        // from property1
        p1.setValue("change only property1");
        assert v.value().equals("change value and property2");
        assert p1.getValue().equals("change only property1");
        assert p2.getValue().equals("change value and property2");

        // from property2
        p2.setValue("update value and property2");
        assert v.value().equals("update value and property2");
        assert p1.getValue().equals("change only property1");
        assert p2.getValue().equals("update value and property2");

        // unsynchronize2
        unsync2.dispose();

        // from value
        v.value("affect only value");
        assert v.value().equals("affect only value");
        assert p1.getValue().equals("change only property1");
        assert p2.getValue().equals("update value and property2");

        // from property2
        p2.setValue("affect only property2");
        assert v.value().equals("affect only value");
        assert p1.getValue().equals("change only property1");
        assert p2.getValue().equals("affect only property2");
    }

    @Test
    void syncFromProperty() {
        StringValue v = new StringValue("123");
        StringProperty p = new SimpleStringProperty("property");
        Disposable unsync = Disposable.empty();

        v.syncFrom(p, unsync);
        assert v.value().equals("property");
        assert p.getValue().equals("property");

        // from value
        v.value("change value only");
        assert v.value().equals("change value only");
        assert p.getValue().equals("property");

        // from property
        p.setValue("change on property");
        assert v.value().equals("change on property");
        assert p.getValue().equals("change on property");

        // unsynchronize
        unsync.dispose();

        // from property
        p.setValue("not sync");
        assert v.value().equals("change on property");
        assert p.getValue().equals("not sync");
    }

    @Test
    void syncFromProperties() {
        StringValue v = new StringValue("123");
        StringProperty p1 = new SimpleStringProperty("property1");
        StringProperty p2 = new SimpleStringProperty("property2");
        Disposable unsync1 = Disposable.empty();
        Disposable unsync2 = Disposable.empty();

        v.syncFrom(p1, unsync1).syncFrom(p2, unsync2);
        assert v.value().equals("property2");
        assert p1.getValue().equals("property1");
        assert p2.getValue().equals("property2");

        // from value
        v.value("not sync");
        assert v.value().equals("not sync");
        assert p1.getValue().equals("property1");
        assert p2.getValue().equals("property2");

        // from property1
        p1.setValue("change on property1");
        assert v.value().equals("change on property1");
        assert p1.getValue().equals("change on property1");
        assert p2.getValue().equals("property2");

        // from property2
        p2.setValue("change on property2");
        assert v.value().equals("change on property2");
        assert p1.getValue().equals("change on property1");
        assert p2.getValue().equals("change on property2");

        // unsynchronize1
        unsync1.dispose();

        // from property1
        p1.setValue("not sync");
        assert v.value().equals("change on property2");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("change on property2");

        // from property2
        p2.setValue("update value and property2");
        assert v.value().equals("update value and property2");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("update value and property2");

        // unsynchronize2
        unsync2.dispose();

        // from property2
        p2.setValue("not affect");
        assert v.value().equals("update value and property2");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("not affect");
    }

    @Test
    void syncToProperty() {
        StringValue v = new StringValue("123");
        StringProperty p = new SimpleStringProperty("property");
        Disposable unsync = Disposable.empty();

        v.syncTo(p, unsync);
        assert v.value().equals("123");
        assert p.getValue().equals("123");

        // from value
        v.value("change value");
        assert v.value().equals("change value");
        assert p.getValue().equals("change value");

        // from property
        p.setValue("not sync");
        assert v.value().equals("change value");
        assert p.getValue().equals("not sync");

        // unsynchronize
        unsync.dispose();

        // from value
        v.value("not affect");
        assert v.value().equals("not affect");
        assert p.getValue().equals("not sync");
    }

    @Test
    void syncToProperties() {
        StringValue v = new StringValue("123");
        StringProperty p1 = new SimpleStringProperty("property1");
        StringProperty p2 = new SimpleStringProperty("property2");
        Disposable unsync1 = Disposable.empty();
        Disposable unsync2 = Disposable.empty();

        v.syncTo(p1, unsync1).syncTo(p2, unsync2);
        assert v.value().equals("123");
        assert p1.getValue().equals("123");
        assert p2.getValue().equals("123");

        // from value
        v.value("456");
        assert v.value().equals("456");
        assert p1.getValue().equals("456");
        assert p2.getValue().equals("456");

        // from property1
        p1.setValue("not sync");
        assert v.value().equals("456");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("456");

        // from property2
        p2.setValue("not affect");
        assert v.value().equals("456");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("not affect");

        // unsynchronize1
        unsync1.dispose();

        // from value
        v.value("change value");
        assert v.value().equals("change value");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("change value");

        // unsynchronize2
        unsync2.dispose();

        // from value
        v.value("change value only");
        assert v.value().equals("change value only");
        assert p1.getValue().equals("not sync");
        assert p2.getValue().equals("change value");
    }

    @Test
    void is() {
        StringValue v = new StringValue("123");
        assert v.is("123") == true;
        assert v.is("456") == false;
    }

    @Test
    void isPredicate() {
        StringValue v = new StringValue("123");
        assert v.is(o -> o.equals("123")) == true;
        assert v.is(o -> o.equals("456")) == false;
    }

    @Test
    void isNot() {
        StringValue v = new StringValue("123");
        assert v.isNot("123") == false;
        assert v.isNot("456") == true;
    }

    @Test
    void isNotPredicate() {
        StringValue v = new StringValue("123");
        assert v.isNot(o -> o.equals("123")) == false;
        assert v.isNot(o -> o.equals("456")) == true;
    }

    /**
     * Simple Implementation.
     */
    private static class StringValue implements ValueHelper<StringValue, String> {

        /** The actual property. */
        private final StringProperty property = new SimpleStringProperty();

        /**
         * With empty value.
         */
        private StringValue() {
        }

        /**
         * With initial value.
         * 
         * @param initialValue
         */
        private StringValue(String initialValue) {
            property.set(initialValue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Property<String> valueProperty() {
            return property;
        }
    }
}