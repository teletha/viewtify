/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.css.Styleable;

import kiss.Disposable;

public interface AutoDisposable extends Disposable {

    /** The css class name to avoid automatic disposing. */
    String AvoidAutoDisposeClass = "avoid-auto-dispose";

    /**
     * Check whether this object supports automatic disposing or not. The default implementation is
     * true.
     * 
     * @return
     */
    default boolean supportAutomaticDispose() {
        return true;
    }

    /**
     * Helper method to check automatic disposing class.
     * 
     * @param styleable
     * @return
     */
    static boolean supportAutomaticDispose(Styleable styleable) {
        return styleable != null && !styleable.getStyleClass().contains(AvoidAutoDisposeClass);
    }
}
