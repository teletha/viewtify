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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import viewtify.ui.View;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DockType {
    /**
     * Define view type.
     * 
     * @return
     */
    Class<? extends View> value();
}
