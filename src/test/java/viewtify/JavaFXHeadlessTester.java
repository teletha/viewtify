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

import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import kiss.I;
import viewtify.ui.View;

@ExtendWith(ApplicationExtension.class)
public abstract class JavaFXHeadlessTester {

    static {
        Viewtify.inHeadless();
        Viewtify.checkHeadlessMode();
    }

    /**
     * Initialize view.
     * 
     * @param <T>
     * @param type
     * @return
     */
    protected final <T extends View> T init(Class<T> type) {
        T view = I.make(type);
        view.ui();
        return view;
    }
}
