/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

import kiss.I;
import viewtify.ui.UILabel;
import viewtify.ui.View;

class UIBuilder {

    private static final MethodHandle UILabelBuilder;

    static {
        try {
            Constructor c = UILabel.class.getDeclaredConstructor(View.class);
            c.setAccessible(true);
            UILabelBuilder = MethodHandles.lookup().unreflectConstructor(c);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Create emtpy {@link UILabel}.
     * 
     * @return
     */
    static UILabel createUILabel() {
        try {
            return (UILabel) UILabelBuilder.invokeExact((View) null);
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }
}
