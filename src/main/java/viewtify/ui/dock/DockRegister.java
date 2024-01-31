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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import kiss.Variable;
import viewtify.ui.UITab;
import viewtify.ui.View;

@Managed(Singleton.class)
public abstract class DockRegister implements Extensible {

    /** The managed independent dock items. */
    private final List<DockItem> independents = new ArrayList();

    /**
     * Initialize and analyze
     */
    protected DockRegister() {
        I.signal(getClass().getDeclaredMethods())
                .take(m -> m.getParameterCount() == 0 && Modifier.isPublic(m.getModifiers()) && m.getReturnType() == void.class)
                .to(m -> {
                    DockItem item = new DockItem(m.getName(), Variable.of(m.getName()), () -> {
                    });

                    independents.add(item);
                });
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected void register(Class<? extends View> view) {
        register(I.make(view));
    }

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected UITab register(View view) {
        Objects.requireNonNull(view);

        return DockSystem.register(view.id()).text(view.title()).contentsLazy(tab -> view);
    }

    /**
     * Query all independent views.
     */
    public List<DockItem> queryIndependentDocks() {
        return independents;
    }
}