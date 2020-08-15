/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package javafx.scene.control.skin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
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
    private static final MethodHandle invoker(String className, String methodName, Class... parameterTypes) {
        try {
            Method method = I.type(className).getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return MethodHandles.lookup().unreflect(method);
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }

    private static final MethodHandle scrollOffsetGetter = invoker("javafx.scene.control.skin.TabPaneSkin$TabHeaderArea", "getScrollOffset");

    private static final MethodHandle scrollOffsetSetter = invoker("javafx.scene.control.skin.TabPaneSkin$TabHeaderArea", "setScrollOffset", double.class);

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
