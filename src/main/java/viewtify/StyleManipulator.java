/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import javafx.beans.property.Property;

import viewtify.ui.helper.PropertyAccessHelper;
import viewtify.ui.helper.PropertyAccessHelper.Type;

public class StyleManipulator {

    public static String get(PropertyAccessHelper css, String name) {
        return get(css.property(Type.Style), name);
    }

    public static String get(Property<String> css, String name) {
        String text = css.getValue();
        if (text == null) {
            return null;
        }

        for (String property : text.split(";")) {
            int i = property.indexOf(name);
            if (i != -1) {
                return property.substring(i + name.length() + 1).trim();
            }
        }
        return null;
    }

    public static void set(PropertyAccessHelper css, String name, String value) {
        set(css.property(Type.Style), name, value);
    }

    public static void set(Property<String> css, String name, String value) {
        String text = css.getValue();
        if (text == null) {
            text = "";
        }

        // set property
        StringBuilder builder = new StringBuilder();
        for (String property : text.split(";")) {
            int i = property.indexOf(name);
            if (i == -1) {
                builder.append(property).append(';');
            }
        }
        builder.append(name).append(':').append(value).append(';');

        // remove garbage
        if (builder.charAt(0) == ';') {
            builder.deleteCharAt(0);
        }

        // assign css
        css.setValue(builder.toString());
    }
}
