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

import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import stylist.Style;
import viewtify.Viewtify;

public interface StyleHelper<Self extends StyleHelper, S extends Styleable> {

    /**
     * Return the associated user interface.
     * 
     * @return
     */
    S ui();

    /**
     * Checks if the specified class has already been applied to this UI.
     * 
     * @param className A target class name.
     * @return Result.
     */
    default boolean hasStyle(String className) {
        return ui().getStyleClass().contains(className);
    }

    /**
     * Apply style class name to user interface.
     * 
     * @param classNames A list of class names to assign.
     * @return Chainable API.
     */
    default Self style(String... classNames) {
        return style(List.of(classNames));
    }

    /**
     * Apply or unapply style class name to user interface.
     * 
     * @param classNames A list of class names to apply or unapply.
     * @return Chainable API.
     */
    default Self style(boolean add, String... classNames) {
        if (add) {
            return style(classNames);
        } else {
            return unstyle(classNames);
        }
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param styles A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self style(Style... styles) {
        return style(I.signal(styles).skipNull().flatArray(Style::className).toList());
    }

    /**
     * Apply or unapply style class name to user interface.
     * 
     * @param styles A list of class names to apply or unapply.
     * @return Chainable API.
     */
    default Self style(boolean add, Style... styles) {
        if (add) {
            return style(styles);
        } else {
            return unstyle(styles);
        }
    }

    /**
     * Apply style to user interface;
     * 
     * @param styles A list of styles to apply.
     * @return Chainable API.
     */
    private Self style(List<String> classNames) {
        Viewtify.inUI(() -> {
            ObservableList<String> classes = ui().getStyleClass();

            for (String className : classNames) {
                if (classes != null && className.length() != 0) {
                    if (!classes.contains(className)) {
                        classes.add(className);
                    }
                }
            }
        });
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param style A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self styleOnly(Style style) {
        if (style != null) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (Style member : style.group()) {
                    String[] names = member.className();

                    if (member == style) {
                        for (String name : names) {
                            if (!classes.contains(name)) {
                                classes.add(name);
                            }
                        }
                    } else {
                        classes.removeAll(names);
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param style A {@link Style} to apply.
     * @return Chainable API.
     */
    default Self styleOnly(ObservableValue<Style> style) {
        style.addListener(o -> styleOnly(style.getValue()));
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param style A {@link Style} to apply.
     * @return Chainable API.
     */
    default Self styleOnly(Variable<Style> style) {
        return styleOnly(style.observing());
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param style A {@link Style} to apply.
     * @return Chainable API.
     */
    default Self styleOnly(Signal<Style> style) {
        style.to(this::styleOnly);
        return (Self) this;
    }

    /**
     * Assign style class name to user interface.
     * 
     * @param timing Assignment timing.
     * @param className A list of class names to assign.
     * @return Chainable API.
     */
    default Self styleWhile(Signal<Boolean> timing, String... className) {
        timing.to(v -> {
            if (v) {
                style(className);
            } else {
                unstyle(className);
            }
        });
        return (Self) this;
    }

    /**
     * Apply {@link Style} to user interface;
     * 
     * @param timing Apply timing.
     * @param styles A list of {@link Style}s to apply.
     * @return Chainable API.
     */
    default Self styleWhile(Signal<Boolean> timing, Style... styles) {
        timing.to(v -> {
            if (v) {
                style(styles);
            } else {
                unstyle(styles);
            }
        });
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
                        classes.removeAll(style.className());
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Unapply class name from user interface;
     * 
     * @param classNames A list of class names to unapply.
     * @return Chainable API.
     */
    default Self unstyle(String... classNames) {
        if (classNames != null && classNames.length != 0) {
            Viewtify.inUI(() -> {
                ObservableList<String> classes = ui().getStyleClass();

                for (String className : classNames) {
                    if (className != null && className.length() != 0) {
                        classes.remove(className);
                    }
                }
            });
        }
        return (Self) this;
    }

    /**
     * Remove all styles.
     * 
     * @return Chainable API
     */
    default Self unstyleAll() {
        ui().getStyleClass().clear();
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