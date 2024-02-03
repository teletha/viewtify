/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import java.lang.StackWalker.Option;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import kiss.WiseConsumer;
import viewtify.ui.View;

@Managed(Singleton.class)
public abstract class DockRegister implements Extensible {

    /** The managed independent dock items. */
    private final List<DockItem> independents = new ArrayList();

    /** The inspection mode. */
    private boolean inspect = true;

    /**
     * Initialize and analyze
     */
    protected DockRegister() {
        I.signal(getClass().getDeclaredMethods())
                .take(m -> m.isAnnotationPresent(DockType.class) && m.getParameterCount() == 0 && Modifier.isPublic(m.getModifiers()) && m
                        .getReturnType() == void.class)
                .sort(Comparator.comparing(Method::getName))
                .to((WiseConsumer<Method>) m -> m.invoke(this));

        inspect = false;
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected void register(Class<? extends View> view) {
        register(estimateId(1), I.make(view), UnaryOperator.identity());
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected void register(Class<? extends View> view, UnaryOperator<DockRecommendedLocation> option) {
        register(estimateId(1), I.make(view), option);
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected void register(View view) {
        register(estimateId(1), view, UnaryOperator.identity());
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected void register(View view, UnaryOperator<DockRecommendedLocation> option) {
        register(estimateId(1), view, option);
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    private void register(String id, View view, UnaryOperator<DockRecommendedLocation> option) {
        Objects.requireNonNull(view);

        if (inspect) {
            independents.add(new DockItem(id, view.title(), () -> register(id, view, option)));
        } else {
            DockSystem.register(id, option).text(view.title()).contentsLazy(tab -> view);
        }
    }

    /**
     * Estimate the dock identifier by method name.
     * 
     * @return
     */
    protected final String estimateId() {
        return estimateId(2);
    }

    /**
     * Estimate the dock identifier by method name.
     * 
     * @param depth
     * @return
     */
    private String estimateId(int depth) {
        String name = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).walk(x -> x.skip(depth + 1).findAny().get()).getMethodName();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Query all independent views.
     */
    public List<DockItem> queryIndependentDocks() {
        return independents;
    }

    /**
     * Request tab registration by id.
     * 
     * @param id
     * @return
     */
    protected boolean queryBy(String id) {
        for (DockItem item : independents) {
            if (item.id().equals(id)) {
                item.registration().run();
                return true;
            }
        }
        return false;
    }
}