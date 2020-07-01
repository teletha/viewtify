/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import kiss.Variable;
import stylist.CSSValue;
import stylist.Properties;
import stylist.Stylist;
import stylist.value.Color;

class CSSProcessor implements Consumer<Properties> {

    /** The digit pattern. */
    private static final Pattern HasDigit = Pattern.compile("[-\\.]?\\d+.+");

    /** The special formatter for JavaFX. */
    public static final Stylist pretty() {
        return Stylist.pretty().color(Color::toRGB).postProcessor(new CSSProcessor());
    }

    /** The property name mapping. */
    private static final Map<String, String> propertyNames = Map.of("color", "text-fill", "stroke-dasharray", "stroke-dash-array");

    /** The property value mapping. */
    private static final Map<String, String> cursorProperties = Map.of("pointer", "hand");

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(Properties properties) {
        properties.compactTo("padding", "0", sides("padding-*"));
        properties.compactTo("margin", "0", sides("margin-*"));
        properties.compactTo("border-width", "0", sides("border-*-width"));
        properties.compactTo("border-style", "solid", sides("border-*-style"));
        properties.compactTo("border-color", Color.Transparent, sides("border-*-color"));
        properties.revalue("cursor", cursorProperties);

        alignment(properties);
        height(properties);
        textIndent(properties);
        userSelect(properties);
        width(properties);

        // assign prefix and map special name
        properties.rename(this::rename);
    }

    /**
     * Rename property names for JavaFX.
     * 
     * @param name
     * @return
     */
    private CSSValue rename(CSSValue propertyName) {
        String name = propertyName.toString();
        String renamed = propertyNames.getOrDefault(name, name);

        return name.equals("visibility") ? propertyName : CSSValue.of("-fx-" + renamed);
    }

    /**
     * Configure alignment.
     * 
     * @param properties
     */
    private void alignment(Properties properties) {
        Variable<CSSValue> horizontal = properties.remove("text-align");
        Variable<CSSValue> vertical = properties.remove("vertical-align");

        if (horizontal.isPresent() || vertical.isPresent()) {
            String value = "";
            String h = horizontal.or(CSSValue.of("left")).toString();
            String v = vertical.or(CSSValue.of("center")).toString();

            if (v.equals("middle")) {
                v = "center";
            }

            if (h.equals("center") && v.equals("center")) {
                value = "center";
            } else {
                value = v + "-" + h;
            }
            properties.set("alignment", CSSValue.of(value));
            if (!value.contains("-")) properties.set("text-alignment", CSSValue.of(value));
        }
    }

    /**
     * Configure key-word height property.
     * 
     * @param properties
     */
    private void height(Properties properties) {
        Variable<CSSValue> height = properties.remove("height");

        if (height.isPresent()) {
            if (HasDigit.matcher(height.v.toString()).matches()) {
                properties.set("pref-height", height.v);
            } else {
                properties.set("height", height.v);
            }
        }
    }

    /**
     * Configure text-indent.
     * 
     * @param properties
     */
    private void textIndent(Properties properties) {
        Variable<CSSValue> indent = properties.remove("text-indent");

        if (indent.isPresent()) {
            properties.set("label-padding", CSSValue.of("0 0 0").join(indent.get()));
        }
    }

    /**
     * Configure user-select.
     * 
     * @param properties
     */
    private void userSelect(Properties properties) {
        Variable<CSSValue> value = properties.remove("user-select");

        if (value.isPresent()) {
            properties.set("focus-traversable", CSSValue.of(!value.get().match("none")));
        }
    }

    /**
     * Configure key-word width property.
     * 
     * @param properties
     */
    private void width(Properties properties) {
        Variable<CSSValue> width = properties.remove("width");

        if (width.isPresent()) {
            if (HasDigit.matcher(width.v.toString()).matches()) {
                properties.set("pref-width", width.v);
            } else {
                properties.set("width", width.v);
            }
        }
    }

    /**
     * Helper method to build side names.
     * 
     * @param template
     * @return
     */
    private static String[] sides(String template) {
        String[] sides = {"top", "right", "bottom", "left"};

        for (int i = 0; i < sides.length; i++) {
            sides[i] = template.replace("*", sides[i]);
        }
        return sides;
    }
}