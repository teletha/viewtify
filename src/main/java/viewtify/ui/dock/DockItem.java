/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import kiss.Variable;
import kiss.WiseRunnable;
import viewtify.ui.View;

public class DockItem {

    public final String id;

    public final Variable<String> title;

    public final WiseRunnable registration;
    
    /**
     * Create dock item for the specified {@link View}.
     * 
     * @param view
     * @param registration
     */
    public DockItem(View view, WiseRunnable registration) {
        this(view.id(), view.title(), registration);
    }

    /**
     * @param id
     * @param title
     * @param registration
     */
    public DockItem(String id, Variable<String> title, WiseRunnable registration) {
        this.id = id;
        this.title = title;
        this.registration = registration;
    }
}
