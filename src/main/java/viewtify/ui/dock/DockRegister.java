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

import java.util.Objects;

import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import viewtify.ui.UITab;
import viewtify.ui.View;

@Managed(Singleton.class)
public abstract class DockRegister implements Extensible {

    /**
     * Register the specified view.
     * 
     * @param view
     */
    protected UITab register(View view) {
        Objects.requireNonNull(view);

        return DockSystem.register(view.id()).text(view.title()).contentsLazy(tab -> view);
    }

    final DockItem match(String id) {
        int index = id.indexOf('@');
        if (index == -1) {
            try {
                // class id
                Class<?> clazz = Class.forName(id);
                if (View.class.isAssignableFrom(clazz)) {

                } else {

                }
            } catch (ClassNotFoundException e) {
                // normal id
            }
        } else {

        }
    }

    private void matchByType(String name, String param) {
        try {
            I.signal(getClass().getMethods()).take(m -> m.getName().equals(name)).take(m -> m.getParameterCount() == 1).to(m -> {

            });
        } catch (ClassNotFoundException e) {
            // name id
        }
    }
}