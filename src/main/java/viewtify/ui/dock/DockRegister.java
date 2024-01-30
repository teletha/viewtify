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
}