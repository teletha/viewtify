/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;

import kiss.Variable;
import stylist.Style;
import viewtify.Viewtify;

/**
 * @version 2018/09/08 19:41:41
 */
public interface StyleHelper<Self extends StyleHelper, S extends Styleable> {

    /**
     * Return the associated user interface.
     * 
     * @return
     */
    S ui();

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param styles A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self style(Style... styles) {
        if (styles != null && styles.length != 0) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (Style style : styles) {
                    if (style != null) {
                        String name = style.name();

                        if (!classes.contains(name)) {
                            classes.add(name);
                        }
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param styles A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self styleOnly(Style style) {
        if (style != null) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (Style member : style.group()) {
                    String clazz = member.name();

                    if (member == style) {
                        if (!classes.contains(clazz)) {
                            classes.add(clazz);
                        }
                    } else {
                        classes.remove(clazz);
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param styles A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self styleOnly(ObservableValue<Style> style) {
        style.addListener(o -> styleOnly(style.getValue()));
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param styles A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self styleOnly(Variable<Style> style) {
        style.observeNow().to(this::styleOnly);
        return (Self) this;
    }

    /**
     * Unapply {@link Style} from user interface;
     * 
     * @param styles A list of {@link Style}s to unapply.
     * @return Chainable API.
     */
    default Self unstyle(Style... styles) {
        if (styles != null && styles.length != 0) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (Style style : styles) {
                    if (style != null) {
                        classes.remove(style.name());
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Create temporary {@link StyleHelper}.
     * 
     * @param styleable
     * @return
     */
    static StyleHelper of(Styleable styleable) {
        return new StyleHelper() {

            /**
             * {@inheritDoc}
             */
            @Override
            public Styleable ui() {
                return styleable;
            }
        };
    }
}
