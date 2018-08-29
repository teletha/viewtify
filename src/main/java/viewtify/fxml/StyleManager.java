/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.fxml;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;

/**
 * @version 2018/08/29 15:01:30
 */
class StyleManager {

    /** The actual manager. */
    private static final Map<Style, String> names = new ConcurrentHashMap();

    static {
        List<StyleDSL> dsls = I.find(StyleDSL.class);

        for (StyleDSL dsl : dsls) {
            for (Field field : dsl.getClass().getDeclaredFields()) {
                if (Style.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);

                    try {
                        Style style = (Style) field.get(null);

                        if (style != null) {
                            names.put(style, field.getName());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        // ignore
                    }
                }
            }
        }
    }

    static String name(Style style) {
        return names.get(style);
    }
}
