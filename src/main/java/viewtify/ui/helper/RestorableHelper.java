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

import java.lang.reflect.Method;

import javafx.beans.property.Property;

import kiss.I;
import viewtify.ui.UserInterface;

public interface RestorableHelper<Self extends RestorableHelper, V> {

    /**
     * The preference.
     * 
     * @return
     */
    Property<V> model();

    /**
     * Set initial value.
     * 
     * @param initialValue
     * @return
     */
    default Self initial(V initialValue) {
        if (this instanceof UserInterface) {
            try {
                UserInterface ui = (UserInterface) this;
                Method method = UserInterface.class.getDeclaredMethod("restore", Property.class, Object.class);
                method.setAccessible(true);
                method.invoke(ui, model(), initialValue);
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }
        return (Self) this;
    }
}
