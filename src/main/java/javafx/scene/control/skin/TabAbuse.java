/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package javafx.scene.control.skin;

import java.lang.reflect.Method;

import javafx.scene.Node;
import javafx.scene.control.TabPane;
import kiss.I;

public class TabAbuse {

    /**
     * Create special method invoker.
     *
     * @param methodName A target method name.
     * @param parameterTypes A list of method parameter types.
     * @return A special method invoker.
     */
    private static final Method invoker(String className, String methodName, Class... parameterTypes) {
        try {
            Method method = I.type(className).getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }

    private static final Method scrollOffsetGetter = invoker("javafx.scene.control.skin.TabPaneSkin$TabHeaderArea", "getScrollOffset");

    private static final Method scrollOffsetSetter = invoker("javafx.scene.control.skin.TabPaneSkin$TabHeaderArea", "setScrollOffset", double.class);

    /**
     * Get the current scroll offset of the specified {@link TabPane}.
     * 
     * @param pane A target.
     * @return A scroll offset (may be negative).
     */
    public static double getTabHeaderScrollOffset(TabPane pane) {
        try {
            return (double) scrollOffsetGetter.invoke(pane.lookup(".tab-header-area"));
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }

    /**
     * Adjust the scroll rate of the tab display part of the specified {@link TabPane}.
     * 
     * @param pane A target.
     * @param diff
     */
    public static void updateTabHeaderScrollOffset(TabPane pane, double diff) {
        try {
            Node header = pane.lookup(".tab-header-area");
            double current = (double) scrollOffsetGetter.invoke(header);
            scrollOffsetSetter.invoke(header, current + diff);
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }
}