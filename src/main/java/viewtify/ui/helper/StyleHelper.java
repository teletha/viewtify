/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;

import kiss.Variable;
import viewtify.Viewtify;

/**
 * @version 2017/12/02 18:19:15
 */
public interface StyleHelper<Self extends StyleHelper, S extends Styleable> {

    /**
     * Return the associated user interface.
     * 
     * @return
     */
    S ui();

    /**
     * Apply class name;
     * 
     * @param className
     */
    default Self style(String className) {
        Viewtify.inUI(() -> {
            ObservableList<String> classes = ui().getStyleClass();

            if (!classes.contains(className)) {
                classes.add(className);
            }
        });
        return (Self) this;
    }

    /**
     * Apply single state class by the specified enum.
     * 
     * @param state
     */
    default <E extends Enum<E>> Self style(E state) {
        if (state != null) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (Enum value : state.getClass().getEnumConstants()) {
                    String name = value.name();

                    if (state == value) {
                        if (!classes.contains(name)) {
                            classes.add(name);
                        }
                    } else {
                        classes.remove(name);
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Apply single state class by the specified enum.
     * 
     * @param node
     * @param state
     */
    default <E extends Enum<E>> Self style(Variable<E> state) {
        return style(Viewtify.calculate(state));
    }

    /**
     * Apply single state class by the specified enum.
     * 
     * @param node
     * @param state
     */
    default <E extends Enum<E>> Self style(ObservableValue<E> state) {
        state.addListener(o -> style(state.getValue()));
        return (Self) this;
    }

    /**
     * Clear all style for the specified enum type.
     * 
     * @param class1
     */
    default <E extends Enum<E>> Self unstyle(Class<E> style) {
        if (style != null) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (Enum value : style.getEnumConstants()) {
                    classes.remove(value.name());
                }
            });
        }
        return (Self) this;
    }
}
