/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.translator;

import kiss.Signal;
import kiss.Variable;

/**
 * 
 */
public enum Lang {

    EN, JP;

    private static Variable<Lang> current = Variable.of(JP);

    public static Lang now() {
        return current.v;
    }

    public static Signal<Lang> observe() {
        return current.observeNow();
    }

    public static void set(Lang lang) {
        if (lang != null) {
            current.set(lang);
        }
    }

    public static void change() {
        if (current.v == EN) {
            set(JP);
        } else {
            set(EN);
        }
    }
}
