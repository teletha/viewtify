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

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.text.Font;

import kiss.I;

public interface PropertyHelper {

    /**
     * Return the JavaFX's UI component.
     * 
     * @return A UI component
     */
    Object ui();

    /**
     * Retrieve the property by {@link Type}.
     * 
     * @param type A property type.
     * @return A property.
     */
    default <T> Property<T> property(Type<T> type) {
        try {
            Object ui = ui();
            return (Property<T>) ui.getClass().getMethod(type.name).invoke(ui);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * 
     */
    class Type<T> {

        /** The defined property type. */
        public static final Type<Font> Font = new Type("font");

        /** The defined property type. */
        public static final Type<Node> Graphic = new Type("graphic");

        /** The defined property type. */
        public static final Type<String> Text = new Type("text");

        /** The property name. */
        private final String name;

        /**
         * 
         */
        private Type(String name) {
            this.name = name + "Property";
        }
    }
}
